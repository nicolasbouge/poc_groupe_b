package controller

import customComponent.AddDataDialog
import model.GlossaryModel
import model.Home
import model.Model
import model.ProjectModel
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import java.awt.FileDialog
import java.awt.Frame
import kotlin.system.exitProcess

class HomeController(val parent : Home) : Controller{
    var currentView by mutableStateOf(parent.viewIdentifier)
    var projectMode by mutableStateOf(false)
    val numberOfButtonPerRow = 8

    fun createMatrix(): MutableList<out List<Model>> {
        val projectModelList = parent.mutableListProject
        val glossaryModelList = parent.mutableListGlossary
        val glossaryChunkedList = glossaryModelList.chunked(numberOfButtonPerRow).toMutableList()
        val projectChunkedList = projectModelList.chunked(numberOfButtonPerRow).toMutableList()
        val chunkedList =
            if (parent.controller.projectMode){
                projectChunkedList
            }else{
                glossaryChunkedList
            }
        return chunkedList
    }
    fun addGlossary(name : String){
        parent.databaseModel.addGlossary(nameAlreadyExistVerification(name, "Glossary"))
        parent.glossaryModelList = parent.databaseModel.getGlossary()
    }
    fun changeHomeView(deleteMode : Boolean) : MutableMap<String, String> {
        val listTextButton : MutableMap<String, String> = mutableMapOf()
        if (projectMode){
            listTextButton["Add"] = "Ajouter un projet"
            listTextButton["Delete"] = "Supprimer un projet"
        }else{
            listTextButton["Add"] = "Ajouter un glossaire"
            listTextButton["Delete"] = "Supprimer un glossaire"
        }
        if(deleteMode){
            listTextButton["Add"] = "Annuler"
            listTextButton["Delete"] = "Valider"
        }
        return listTextButton
    }
    fun addProject(name : String){
        val fileDialog = FileDialog(Frame(), "Sélectionner des fichiers", FileDialog.LOAD)
        fileDialog.isMultipleMode = true
        fileDialog.isVisible = true
        val selectedFiles = mutableListOf<String>()
        fileDialog.files.forEach { file ->
            selectedFiles.add(file.absolutePath)
        }
        parent.databaseModel.addProject(nameAlreadyExistVerification(name, "Project"), selectedFiles)
        parent.projectList = parent.databaseModel.getProject()
        try{
            fileDialog.dispose()
        } catch (e : Exception){
            println("Une erreur est survenue lors de la fermeture de la fenêtre de sélection de fichier")
        }
    }
    fun deleteProject(selectedProjects : MutableList<Int>){
        selectedProjects.forEach { projectId ->
            parent.databaseModel.deleteProject(projectId)
        }
        parent.controller.updateListProject(parent.databaseModel.getProject())
    }
    fun updateListGlossary(newList : List<GlossaryModel>){
        parent.mutableListGlossary = newList
        parent.glossaryModelList = parent.mutableListGlossary
    }
    fun updateListProject(newList : List<ProjectModel>){
        parent.mutableListProject = newList
        parent.projectList = parent.mutableListProject
    }
    fun nameAlreadyExistVerification(newProjectName : String, type : String) : String{
        var modelList : List<Model>? = null
        when(type){
            "Project" -> modelList = parent.projectList
            "Glossary" -> modelList = parent.glossaryModelList
        }
        if (modelList != null){
            var name = newProjectName
            var numberOfProjectWithSimilarName = 0
            modelList.forEach { model ->
                if (model.name == name){
                    numberOfProjectWithSimilarName += 1
                    name = "$newProjectName ($numberOfProjectWithSimilarName)"
                }
            }
            return name
        }
        return newProjectName
    }
    fun deleteGlossary(selectedGlossary : MutableList<Int>){
        selectedGlossary.forEach { glossaryId ->
            parent.databaseModel.deleteGlossary(glossaryId)
        }
        parent.controller.updateListGlossary(parent.databaseModel.getGlossary())
    }
    fun buttonDelete(selectedElement : MutableList<Int>) : MutableList<Int>{
        if (projectMode){
            deleteProject(selectedElement)
        }else{
            deleteGlossary(selectedElement)
        }
        return mutableListOf()
    }
    //Composable functions
    @Composable
    fun show(projectMode: Boolean){
        this.projectMode = projectMode
        if (currentView != parent.viewIdentifier){
            NavigationController().Navigator(parent.glossaryModelList, currentView, parent.glossaryId, parent.projectId, projectMode)
        }else{
            parent.display.main()
        }
    }
    @Composable
    fun chooseName() : Boolean{
        var visibility by remember { mutableStateOf(true) }
        if (visibility){
            AddDataDialog(
                onDismiss = {visibility = false},
                onSave = {name ->
                    if(isNameNotBlank(name)){
                        if (projectMode){
                            handleProjectMode(name)
                        }
                        else{
                            handleGlossaryMode(name)
                        }
                        visibility = false
                    }
                },
                choosenContent = { getChoosenContent() },
                choosenTitle = { getChoosenTitle() }
            )
        }
        return visibility
    }

    fun isNameNotBlank(name: MutableState<List<String>>): Boolean {
        return name.value[0].isNotBlank()
    }

    fun handleProjectMode(name: MutableState<List<String>>) {
        addProject(name.value[0])
        updateListProject(parent.projectList)
    }

    private fun handleGlossaryMode(name: MutableState<List<String>>) {
        addGlossary(name.value[0])
        updateListGlossary(parent.glossaryModelList)
    }

    @Composable
    fun getChoosenContent(): List<String> {
        return if (projectMode){
            listOf("Veuillez saisir le nom du projet")
        }else{
            listOf("Veuillez saisir le nom du glossaire")
        }
    }

    @Composable
    private fun getChoosenTitle(): @Composable () -> Unit {
        return if (projectMode){
            { Text("Ajouter un projet") }
        }else{
            { Text("Ajouter un glossaire") }
        }
    }

    @Composable
    fun changeColor(projectMode : Boolean) : ButtonColors{
        return if (projectMode){
            ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = Color.Blue)
        }
        else{
            ButtonDefaults.buttonColors(backgroundColor = Color.LightGray, contentColor = Color.Blue)
        }
    }
    @Composable
    fun exit() : Boolean{
        var closePopUp by remember{mutableStateOf(true)}
        if (closePopUp){
            AddDataDialog(
                onDismiss = {closePopUp = false},
                onSave = {closePopUp = false; exitProcess(1) },
                choosenContent = {Text("Êtes vous sûr de vouloir quitter ?"); listOf()},
                choosenTitle = { Text("Quitter") }
            )
        }
        return closePopUp

    }
}