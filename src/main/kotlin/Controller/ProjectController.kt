package Controller

import CustomComponent.AddDataDialog
import Model.GlossaryModel
import Model.Project
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*

class ProjectController(private val parent : Project) : Controller {
    var currentView by mutableStateOf(2)
    var matrixOfValues by mutableStateOf(mutableListOf(listOf("")))
    var glossarySelected by mutableStateOf(false)
    private lateinit var choosenGlossary : MutableState<GlossaryModel>
    @Composable
    fun goHome(){
        NavigationController().Navigator(parent.glossaryModelList, 0, parent.glossaryId, parent.projectId, true)
    }
    @Composable
    fun show(){
        if (currentView != 2){
            goHome()
        }else{
            parent.display.main()
        }
    }
    @Composable
    fun compareButtonClicked(): Boolean {
        var open by remember{mutableStateOf(true)}
        if (open){
            AddDataDialog(
                onDismiss = {open = false},
                onSave = {list->
                    list.value.forEach {
                        choosenGlossary = parent.databaseModel.getGlossaryByName(it)?.let { it1 -> mutableStateOf(it1) }!!
                    }
                    open = false},
                choosenContent = {selectGlossary()},
                choosenTitle = {Text("Erreur lors de la tentative")}
            )
        }
        return false
    }
    @Composable
    fun errorMessage(){
        var open by remember { mutableStateOf(true) }
        if (open){
            AddDataDialog(
                onDismiss = {open = false},
                onSave = {open = false},
                choosenContent = {
                    Text("Pour pouvoir comparer les termes métier du code avec un glossaire, il faut un glossaire." + "\nVeuillez créer un glossaire dans le menu principale")
                    listOf() },
                choosenTitle = {Text("Erreur")}
            )
        }

    }
    @Composable
    private fun selectGlossary() : List<String>{
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf("") }
        val options : MutableList<String> = mutableListOf()
        parent.databaseModel.getGlossary().forEach { options.add(it.name); println(it.name) }
        Column {
            Text("Veuillez séectionnez un glossaire pour afficher la comparaison")
            Row{
                BasicTextField(
                    value = selectedOption,
                    onValueChange = {},
                    textStyle = MaterialTheme.typography.body1
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                selectedOption = option
                                expanded = false
                            }
                        ) {
                            Text(text = option)
                        }
                    }
                }
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

        }
        return listOf(selectedOption)
    }
    fun parseFiles(): MutableList<List<String>> {
        val listPath: List<String> = parent.databaseModel.getPath(parent.projectId)
        matrixOfValues = ParserController.extractTextFromFolder(listPath)
        return matrixOfValues
    }

}