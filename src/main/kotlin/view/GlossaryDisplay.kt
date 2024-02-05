package view

import model.Glossary
import customComponent.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class GlossaryDisplay(private val parent : Glossary) : Display {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun main() {
        var isDialogVisible by remember { mutableStateOf(false) }
        var boardColumn by remember { mutableStateOf(BoardValues.GLOBAL) }
        var glossaryWords by remember { mutableStateOf(parent.databaseModel.getWordsAsMatrix(parent.glossaryId))}
        var matrixValues : MutableList<List<String>> by remember { mutableStateOf(parent.controller.filterValue(boardColumn,glossaryWords).toMutableList()) }
        var contextPosition by remember { mutableStateOf(2) }
        var termePosition by remember { mutableStateOf(0) }
        var alreadyExistError by remember { mutableStateOf(false) }

        //Fond
        Row(modifier = Modifier.fillMaxSize(1f)) {
            //Première colonne avec les boutons d'actions
            Column(
                modifier = Modifier.background(Color.LightGray).fillMaxHeight(1f).weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                var active by remember { mutableStateOf(false) }
                //Colonne avec le bouton de retour
                Column(modifier = Modifier.weight(1f).padding(top = 20.dp), verticalArrangement = Arrangement.Top){
                    //Bouton de retour
                    Button(
                        modifier = Modifier.fillMaxWidth(1f),
                        onClick = { parent.controller.currentView = 0 }, shape = RoundedCornerShape(0),
                        colors = ButtonDefaults.buttonColors(Color(0xFF78BAF7)))
                    {
                        Text("Retour")
                    }
                }
                //Colonne avec le bouton d'ajout de terme
                Column (
                    modifier = Modifier.weight(1f).padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    //Bouton d'ajout de nouveau terme
                    Button(
                        onClick = { isDialogVisible = true },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .onPointerEvent(PointerEventType.Enter){active = true}
                            .onPointerEvent(PointerEventType.Exit){active = false},
                        colors = if (active)
                            ButtonDefaults.buttonColors(
                                backgroundColor = Color(47, 155, 255),
                                contentColor = Color.White,
                            ) else ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Black,
                        ),
                        border = BorderStroke(width = 1.dp, Color.Black)
                    ) {
                        Text("Nouveau Terme")
                    }
                }
            }
            //Colone du tableau
            Column(modifier = Modifier.weight(8f)){
                //Nom du glossaire
                Text(
                    modifier = Modifier.fillMaxWidth(1f).weight(1f),
                    textAlign = TextAlign.Center,
                    text = parent.databaseModel.getGlossary(parent.glossaryId).name,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                //ligne avec les différents filtre
                Row(modifier = Modifier.padding(start  =20.dp).weight(1f)){
                    Button(onClick = {boardColumn = BoardValues.GLOBAL
                        matrixValues = parent.controller.updateWordMatrix(glossaryWords, boardColumn)
                        contextPosition = 2
                        termePosition = 0},
                        colors = ButtonDefaults.buttonColors(Color(0xFF78BAF7))
                    )
                    {Text("Global")}

                    Spacer(modifier = Modifier.padding(end = 20.dp))

                    Button(onClick = {boardColumn = BoardValues.CONTEXTE
                        matrixValues = parent.controller.updateWordMatrix(glossaryWords, boardColumn)
                        contextPosition = 1
                        termePosition = 0},
                        colors = ButtonDefaults.buttonColors(Color(0xFF78BAF7))
                    )
                    {Text("Contexte")}
                }
                //Tableau des vues
                TableView(
                    customModifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()).weight(10f),
                    boardValues = matrixValues,
                    columnList = boardColumn.listColumnName,
                    isThereActions = true,
                    termePosition = termePosition,
                    contextPosition = contextPosition,
                    onDelete = {
                        terme, concept ->
                            val word = parent.databaseModel.getWord(terme,concept,parent.glossaryId)
                            if (word != null) {
                                parent.databaseModel.deleteWord(parent.glossaryId, word.idWord)
                                matrixValues = parent.databaseModel.getWordsAsMatrix(parent.glossaryId)
                                glossaryWords = parent.databaseModel.getWordsAsMatrix(parent.glossaryId)
                            }
                    },
                    onUpdate = {
                        terme, concept, newWord->
                            val oldWord = parent.databaseModel.getWord(terme,concept,parent.glossaryId)
                            if (oldWord != null) {
                                if (newWord.value.isNotEmpty()) {
                                    if (newWord.value[termePosition].isNotBlank() && newWord.value[contextPosition].isNotBlank()) {
                                        alreadyExistError = parent.controller.editWord(newWord.value, parent.glossaryId, oldWord)
                                        matrixValues = parent.databaseModel.getWordsAsMatrix(parent.glossaryId)
                                        glossaryWords = parent.databaseModel.getWordsAsMatrix(parent.glossaryId)
                                    }else{
                                        println("Veuillez rajouter un contexte et un label au mot")
                                    }
                                }else{
                                    println("pas de nouveau mot")
                                }
                            }else{
                                println("l'ancien mot n'a pas été trouvé")
                            }
                    },
                    glossaryId = parent.glossaryId
                )

            }
        }
        if (isDialogVisible) {
            isDialogVisible = parent.controller.chooseName()
            glossaryWords = parent.controller.getWords()
            matrixValues = parent.controller.updateWordMatrix(glossaryWords, boardColumn)
        }
        if (alreadyExistError) {
            ErrorWindow("Ce terme existe déjà", onDismiss = {alreadyExistError = false})
        }
    }

}