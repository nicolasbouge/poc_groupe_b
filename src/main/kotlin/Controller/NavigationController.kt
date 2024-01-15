package Controller

import Model.GlossaryModel
import Model.Home
import Model.Glossary
import Model.Project
import androidx.compose.runtime.Composable

class NavigationController() : Controller {

    @Composable
    fun Navigator(listGlossary : List<GlossaryModel>, currentView : Int, glossaryId : Int?, projectId : Int?, projectMode : Boolean){
        val homeView = Home(listGlossary)
        val glossaryView = glossaryId?.let { Glossary(it,listGlossary) }
        val projectView = projectId?.let { Project(listGlossary, it) }
        when(currentView){
            0 -> homeView.controller.show(projectMode)
            1 -> glossaryView?.controller?.show()
            2 -> projectView?.controller?.show()
        }
    }
}