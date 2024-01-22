package CustomComponent

import androidx.compose.material.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddDataDialog(onDismiss: () -> Unit, onSave: (MutableState<List<String>>) -> Unit, choosenContent: @Composable () -> List<String>, choosenTitle : @Composable () -> Unit) {
    var textValues = remember { mutableStateOf(listOf("")) }
    AlertDialog(
        onDismissRequest = { onDismiss()},
        title = {
            choosenTitle()},
        text = {
           textValues = mutableStateOf(choosenContent())
        },
        confirmButton = {
            TextButton(onClick = {onSave(textValues)}){
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Annuler")
            }
        },
    )
}