package customComponent

import model.DatabaseModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

val databaseModel = DatabaseModel()

@Composable
fun TableView(
    customModifier: Modifier,
    boardValues: MutableList<List<String>>,
    columnList: List<String>,
    isThereActions: Boolean,
    actionsConfig: ActionsConfig? = null,
) {
    var count = 0
    var deleteWord by remember { mutableStateOf(false) }
    var editWord by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf(listOf<String>()) }
    MaterialTheme {
        Column(
            modifier = customModifier
        ) {
            // Intitulé de chaque colonne
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF78BAF7))
                    .padding(8.dp)
            ) {
                if (isThereActions) {
                    Text("Action", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
                columnList.forEach { text ->
                    Text(text, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }

            // Donné
            boardValues.forEach { listString ->
                count++
                val rowColor: Color = if (count % 2 != 0) {
                    Color.White
                } else {
                    Color.LightGray
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowColor)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isThereActions && actionsConfig != null && actionsConfig.contextPosition != null &&
                        actionsConfig.termePosition != null
                    ) {
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                            IconButton(onClick = { deleteWord = true; selectedWord = listString }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                            IconButton(onClick = { editWord = true; selectedWord = listString }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                            }
                            if (editWord && actionsConfig.glossaryId != null) {
                                AddDataDialog(
                                    onDismiss = { editWord = false },
                                    onSave = { word ->
                                        editWord = false
                                        if (actionsConfig.onUpdate != null) {
                                            actionsConfig.onUpdate?.let {
                                                it(
                                                    selectedWord[actionsConfig.termePosition],
                                                    selectedWord[actionsConfig.contextPosition],
                                                    word
                                                )
                                            }
                                        } else {
                                            println("Veuillez ajouter la fonction de modification")
                                        }
                                    },
                                    choosenContent = { AddWordForm(listOf("Terme", "Définition", "Contexte", "Synonyme", "Antonyme", "Lié à")) },
                                    choosenTitle = { Text("Ajout d'un nouveau terme") }
                                )
                            }
                            if (deleteWord) {
                                AddDataDialog(
                                    onDismiss = { deleteWord = false },
                                    onSave = { _ ->
                                        deleteWord = false
                                        if (actionsConfig.onDelete != null) {
                                            actionsConfig.onDelete?.let {
                                                it(
                                                    selectedWord[actionsConfig.termePosition],
                                                    selectedWord[actionsConfig.contextPosition]
                                                )
                                            }
                                        } else {
                                            println("Veuillez ajouter la fonction de suppression")
                                        }
                                    },
                                    choosenTitle = { Text("Supression de mot") },
                                    choosenContent = {
                                        Text("Voulez vous vraiment supprimé ce mot : ${selectedWord[0]}?")
                                        listOf("Voulez vous vraiment supprimé ce mot ?")
                                    }
                                )
                            }
                        }
                    }
                    listString.forEach {
                        Text(text = it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

data class ActionsConfig(
    val onDelete: ((terme: String, concept: String) -> Unit)? = null,
    val onUpdate: ((terme: String, concept: String, newWord: MutableState<List<String>>) -> Unit)? = null,
    val contextPosition: Int? = null,
    val termePosition: Int? = null,
    val glossaryId: Int? = null
)
