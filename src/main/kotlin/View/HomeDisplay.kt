package View

import Model.Home
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import Model.Model
import androidx.compose.foundation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

class HomeDisplay(private val parent : Home) : Display {
    private var deleteButtonPressed by mutableStateOf(false)
    private var selectedProjects: MutableList<Int> by mutableStateOf(mutableListOf())
    private var visible by mutableStateOf(false)

    private val controller = parent.controller

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun main() {
        val chunkedList : MutableList<out List<Model>> = controller.createMatrix()
        var exit by remember {mutableStateOf(false)}
        val listText = controller.changeHomeView(deleteButtonPressed)
        val topButtonStyle = TextStyle(fontSize = 13.sp,fontWeight = FontWeight(400),color = Color(0xFF000000),textAlign = TextAlign.Center)
        val navigationButtonModifier =  Modifier.fillMaxHeight().width(140.dp)
        if (visible) {
            visible = controller.chooseName()
        }
        if (exit){
            exit = controller.exit()
        }
        MaterialTheme {
            //Fond
            Column(Modifier.background(Color.LightGray)) {
                //Onglet de Navigation
                Row(Modifier.weight(2f).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                    Row{
                        TextButton(modifier = navigationButtonModifier,
                            colors =controller.changeColor(!controller.projectMode),
                            onClick = {controller.projectMode = false}){Text("Glossaire")}
                        TextButton(modifier = navigationButtonModifier,
                            colors = controller.changeColor(controller.projectMode),
                            onClick = {controller.projectMode = true}){Text("Projet")}
                    }
                    //Bouton de suppression
                    IconButton(
                        onClick = { exit = true},
                        modifier = Modifier
                            .height(IntrinsicSize.Max)
                            .width(140.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                //Haut de page
                Row(Modifier.fillMaxWidth().background(Color.White).weight(2f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Trait de gauche
                    Box(Modifier.weight(1f).height(2.dp).background(Color(0xFF78BAF7)))
                    //Bouton d'ajout
                    TextButton(
                        onClick = {
                            addButtonOnClick()
                        },
                        modifier = Modifier
                            .border(
                                width = 3.dp,
                                color = Color(0xFF78BAF7),
                                shape = RoundedCornerShape(size = 10.dp),
                            )
                            .width(150.dp)
                            .height(40.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(size = 10.dp)
                            )
                    ) {
                        listText["Add"]?.let {
                            Text(
                                text = it,
                                style = topButtonStyle
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .height(2.dp)
                            .background(Color(0xFF78BAF7))
                    )
                    // Bouton "Supprimer un projet"
                    TextButton(
                        onClick = {
                            deleteButtonOnClick()
                        },
                        modifier = Modifier
                            .border(
                                width = 3.dp,
                                color = Color(0xFF78BAF7),
                                shape = RoundedCornerShape(size = 10.dp),
                            )
                            .width(150.dp)
                            .height(40.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(size = 10.dp)
                            )

                    ) {
                        listText["Delete"]?.let {
                            Text(
                                text = it,
                                style = topButtonStyle
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(Color(0xFF78BAF7))
                    )
                }
                //Affichage des éléments
                chunkedList.forEach { chunk ->
                    //Ligne de Boutons
                    Row(
                        modifier = Modifier
                            .weight(28f)
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    ) {
                        chunk.forEach { element ->
                            var isSelected by remember { mutableStateOf(false) }
                            var hovered by remember { mutableStateOf(false) }
                            if (!deleteButtonPressed){isSelected = false}
                            //Il y a une ligne car le bouton a aussi une checkBox qui peut s'afficher si on est en deleteMode
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                TextButton(
                                    modifier = Modifier
                                        .onPointerEvent(PointerEventType.Enter) { hovered = true }
                                        .onPointerEvent(PointerEventType.Exit) { hovered = false }
                                        .weight(1f),
                                    onClick = {
                                        if (deleteButtonPressed) {
                                            isSelected = !isSelected
                                            selectElement(isSelected, element.id)
                                        } else {
                                            if (controller.projectMode){
                                                controller.currentView = 2
                                                parent.projectId = element.id
                                            }else{
                                                controller.currentView = 1
                                                parent.glossaryId = element.id
                                            }
                                        }
                                    },
                                    colors = hoverButton(hovered),
                                ) {
                                    Text(element.name)
                                    if (deleteButtonPressed) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = {
                                                selectElement(it, element.id)
                                            },
                                            modifier = Modifier.height(10.dp).width(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                        //Permet de créer l'espace manquant afin de garder un affichage uniforme
                        if (chunk.size != controller.numberOfButtonPerRow) {
                            val spacerSize = controller.numberOfButtonPerRow - chunk.size
                            Spacer(
                                modifier = Modifier.weight(spacerSize.toFloat())
                            )
                        }
                    }
                }
                //permet de garder l'affichage normal si jamais la liste est vide
                if (chunkedList.isEmpty()){
                    Spacer(Modifier.weight(28f).fillMaxWidth().background(Color.White).padding(8.dp, 0.dp, 8.dp, 0.dp))
                }
            }
        }
    }
    @Composable
    private fun hoverButton(hovered: Boolean): ButtonColors {
        return if (hovered)
            ButtonDefaults.buttonColors(
                backgroundColor = Color(47, 155, 255), contentColor = Color.White,
            ) else
            ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF78BAF7), contentColor = Color.Black,
            )
    }
    private fun selectElement(selected: Boolean, elementId: Int) {
        if (deleteButtonPressed) {
            if (selected) {
                // Ajoute l'ID du projet à la liste
                selectedProjects += elementId
            } else {
                // Retire l'ID du projet de la liste
                selectedProjects -= elementId
            }
        }
    }
    private fun addButtonOnClick(){
        when (deleteButtonPressed) {
            true -> {
                deleteButtonPressed = false
                selectedProjects = mutableListOf()
            }
            false -> {
                visible = true
            }
        }
    }
    private fun deleteButtonOnClick(){
        if (deleteButtonPressed) {
            selectedProjects = controller.buttonDelete(selectedProjects)
            deleteButtonPressed = false
        } else {
            deleteButtonPressed = true
        }
    }
}