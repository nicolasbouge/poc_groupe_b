package Controller

import Model.WordModel
import Model.Glossary
import BoardValues
import CustomComponent.AddDataDialog
import CustomComponent.AddWordForm
import CustomComponent.ErrorWindow
import CustomComponent.databaseModel
import androidx.compose.material.Text
import androidx.compose.runtime.*

class GlossaryController(private val parent : Glossary) : Controller {
    var currentView by mutableStateOf(parent.viewIdentifier)
    @Composable
    fun show(){
        if (currentView != parent.viewIdentifier){
            NavigationController().Navigator(parent.glossaryModelList, currentView, parent.glossaryId, parent.projectId, false)
        }else{
            parent.display.main()
        }
    }
    fun updateWordMatrix(newValue : MutableList<List<String>>, filterValue : BoardValues): MutableList<List<String>> {
        return parent.controller.filterValue(filterValue, newValue).toMutableList()
    }

    private fun identicalWords(listWords : List<WordModel>, newWordModel : WordModel) : Boolean{
        var sameWord = false
        if (listWords.isNotEmpty()){
            listWords.forEach { word ->
                if (word.label == newWordModel.label && newWordModel.contextePrimaire == word.contextePrimaire && !sameWord){
                    sameWord = true
                }
            }
        }

        return sameWord
    }
    @Composable
    fun chooseName() : Boolean{
        var visibility by remember { mutableStateOf(true) }
        var alreadyExistError by remember { mutableStateOf(false) }
        var notCompleteForm by remember{ mutableStateOf(false) }
        if (visibility){
            AddDataDialog(
                onDismiss = {visibility = false},
                onSave = {word ->
                    alreadyExistError = checkWordExistence(word)
                    notCompleteForm = checkFormCompletion(word)
                    if(!alreadyExistError && !notCompleteForm){
                        visibility=false
                    }
                },
                choosenContent = { AddWordForm(listOf("Terme", "Définition", "Contexte", "Synonyme", "Antonyme", "Lié à")) },
                choosenTitle = { Text("Ajout d'un nouveau terme") }
            )
        }
        displayErrorWindow(alreadyExistError, "Le mot existe déja", { alreadyExistError = false })
        displayErrorWindow(notCompleteForm, "Vous devez renseigner les cases Terme et Contexte", { notCompleteForm = false })

        return visibility
    }

    private fun checkWordExistence(word: MutableState<List<String>>): Boolean {
        return if (word.value.isNotEmpty()){
            if (word.value[0].isNotBlank() && word.value[2].isNotBlank()){
                addWord(word.value)
            }else{
                false
            }
        } else {
            false
        }
    }

    private fun checkFormCompletion(word: MutableState<List<String>>): Boolean {
        return word.value[0].isBlank() || word.value[2].isBlank()
    }

    @Composable
    private fun displayErrorWindow(errorState: Boolean, errorMessage: String, onDismiss: () -> Unit) {
        if (errorState){
            ErrorWindow(errorMessage, onDismiss = onDismiss)
        }
    }

    fun getWords() : MutableList<List<String>>{
        return parent.databaseModel.getWordsAsMatrix(parent.glossaryId).toMutableList()
    }

    fun editWord(word : List<String>, glossaryId: Int, oldWord: WordModel): Boolean{
        val listWords = databaseModel.getWords(glossaryId)
        val newWordModel = WordModel(glossaryId, 0,word[0], word[1],word[2], word[3], word[4], word[5])
        if (!identicalWords(listWords, newWordModel) || (oldWord.label == word[0] && oldWord.contextePrimaire == word[2])){
            databaseModel.updateWord(newWordModel, oldWord)
        }else{
            println("le mot sélectionné ne peut avoir le même label ou contexte qu'un mot déjà existant")
        }
        return identicalWords(listWords, newWordModel)
    }
    private fun addWord(word : List<String>): Boolean{
        val listWords = parent.databaseModel.getWords(parent.glossaryId)
        val newWordModel = WordModel(parent.glossaryId, 0,word[0], word[1],word[2], word[3], word[4], word[5])
        if (!identicalWords(listWords, newWordModel)){
            parent.databaseModel.addWord(newWordModel)
        }
        return identicalWords(listWords, newWordModel)
    }

    fun filterValue(filterType : BoardValues, matrixValues : MutableList<List<String>>): MutableList<List<String>> {
        val newMatrix : MutableList<List<String>> = mutableListOf()
        matrixValues.forEach{ value ->
            val listString :List<String> =
                when (filterType){
                    BoardValues.GLOBAL -> value
                    BoardValues.CONTEXTE -> listOf(value[0], value[2])
                    BoardValues.PARSERGLOBAL -> value
                    BoardValues.PARSERFILE -> listOf(value[0],value[1])
                }
            newMatrix.add(listString)
        }

        return newMatrix
    }
}