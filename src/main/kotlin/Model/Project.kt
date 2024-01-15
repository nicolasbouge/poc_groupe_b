package Model

import Controller.ProjectController
import View.ProjectDisplay

class Project(override val glossaryModelList: List<GlossaryModel>, override val projectId: Int) : ViewInterface<ProjectDisplay, ProjectController> {
    override val glossaryId: Int? = null
    override val viewIdentifier: Int = 2
    override val display: ProjectDisplay = ProjectDisplay(this)
    override val controller: ProjectController = ProjectController(this)
    override val databaseModel = DatabaseModel()

    fun getProjects(): List<ProjectModel> {
        return databaseModel.getProject()
    }
    fun getProject(): ProjectModel? {
        return databaseModel.getProject(projectId)
    }
}