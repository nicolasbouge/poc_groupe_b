import Controller.NavigationController
import Model.DatabaseModel
import Model.GlossaryModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.window.*
import java.util.*

@Composable
fun MainView() {
    //Récupération de la base de donné
    val databaseModel = DatabaseModel()
    databaseModel.getConnection()
    databaseModel.createDatabase()
    val glossaryList : List<GlossaryModel> = databaseModel.getGlossary()
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
       NavigationController().Navigator(glossaryList,0, null, null, false)
    }
}

//Fonction de lancement de l'application
fun main() = application {
    val os = System.getProperty("os.name").lowercase(Locale.getDefault())
    val mac = os.contains("mac")
    Window(
        title = "POC SAE",
        onCloseRequest = { exitApplication() },
        undecorated = !mac,
        resizable = false,
        state = rememberWindowState(placement = WindowPlacement.Maximized, position = WindowPosition.Aligned(alignment = Center))
        ){
        MainView()
    }
}
