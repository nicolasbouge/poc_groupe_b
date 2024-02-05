package customComponent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun AddWordForm(labels: List<String>): List<String> {
    // Utilisation de List pour initialiser la liste avec des chaînes vides
    val textValues = remember { mutableStateListOf(*List(labels.size) { "" }.toTypedArray()) }
    Column(modifier = Modifier.height(400.dp).width(300.dp).verticalScroll(rememberScrollState())) {
        labels.forEachIndexed { index, label ->
            var textValue by remember { mutableStateOf(textValues.getOrNull(index) ?: "") }
            Text(label)
            Box(
                modifier = Modifier.fillMaxWidth(1f).heightIn(max = 200.dp))
            {
                TextField(
                    value = textValue,
                    onValueChange = {
                        if (it.length + textValue.length < 300){
                            textValue = it
                            textValues[index] = it
                        }
                    },
                    maxLines = 3
                )
            }

        }
    }

    return textValues
}
