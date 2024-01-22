package CustomComponent

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErrorWindow(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text("Erreur")
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(errorMessage)
                Spacer(modifier = Modifier.height(16.dp))

            }
        }, buttons = {
            Button(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Fermer")
            }
        }
    )
}

// Utilisation :
// ErrorDialog("Votre message d'erreur ici", onDismiss = { /* Gérer la fermeture de la fenêtre d'erreur */ })
