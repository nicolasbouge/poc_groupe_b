package view

import customComponent.*
import model.Project
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.system.exitProcess

private const val termesTrouves = "Termes Trouvé"
class ProjectDisplay (private val parent : Project): Display{
    private var displayMode by mutableStateOf(termesTrouves)
    private var matrixOfValues by mutableStateOf(mutableListOf(listOf("")))
    @Composable
    override fun main() {
        if (!parent.controller.glossarySelected && displayMode == "Comparaison" && parent.databaseModel.getGlossary().isNotEmpty()){
            println("choix du glossaire effectuén affichage de la table nécessaire")
            matrixOfValues = parent.controller.matrixOfValues
        }
        else if(displayMode == "Comparaison" && parent.databaseModel.getGlossary().isEmpty()){
            parent.controller.errorMessage()
        }
        //Fond
        Row {
            //Colonne avec les boutons d'actions
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().background(Color.LightGray),
                verticalArrangement = Arrangement.Top
            ) {
                //Bouton de retour
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF78BAF7)),
                    onClick = {parent.controller.currentView = 0}
                ){Text("Retour")}
                //Espace
                Spacer(modifier = Modifier.padding(bottom = 20.dp))
                //Bouton de Parsing
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF78BAF7)),
                    onClick = {matrixOfValues = parent.controller.parseFiles()}
                ){Text("Parsing")}
            }
            Column(
                modifier = Modifier.weight(7f),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                //Titre et bouton pour quitter
                Row{
                    Text(
                        text = "${parent.getProject()?.name}",
                        modifier = Modifier.weight(9f),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick = { exitProcess(1) },
                        modifier = Modifier.weight(1f).background(Color.Red)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                displayModeButtons(Modifier.fillMaxWidth().padding(start = 16.dp))
                if (displayMode == "Comparaison" || displayMode == termesTrouves){
                    TableView(
                        customModifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                        boardValues = matrixOfValues,
                        columnList = BoardValues.PARSERGLOBAL.listColumnName,
                        isThereActions = false
                    )
                }
            }
        }
    }
    @Composable
    private fun displayModeButtons(modifier : Modifier){
        val listDisplayModeName = listOf(termesTrouves, "Comparaison", "Graphique")
        Row (horizontalArrangement = Arrangement.Start, modifier = modifier){
            listDisplayModeName.forEach {
                TextButton(onClick= {displayMode = it}){Text( it)}
                Spacer(Modifier.padding(start = 10.dp))
            }
        }
    }
}