package Model

import Controller.HomeController
import View.HomeDisplay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Home(override var glossaryModelList: List<GlossaryModel>) : ViewInterface<HomeDisplay, HomeController> {
    override val viewIdentifier: Int = 0
    override var glossaryId: Int? = null
    override var projectId: Int? = null
    override val databaseModel : DatabaseModel = DatabaseModel()
    var projectList : List<ProjectModel> = databaseModel.getProject()
    var mutableListProject by mutableStateOf(projectList)
    var mutableListGlossary by mutableStateOf(glossaryModelList)
    override val controller: HomeController = HomeController(this)
    override val display: HomeDisplay = HomeDisplay(this)

}