import androidx.compose.runtime.mutableStateOf
import controller.HomeController
import model.GlossaryModel
import org.junit.Test
import model.Home
import model.ProjectModel

import org.junit.jupiter.api.Assertions.*
import java.awt.FileDialog
import java.awt.Frame

class HomeControllerTest {

    @Test
    fun createMatrixTest() {
        // Créer des exemples de projets
        val projectList = listOf(
            ProjectModel(1, "Projet 1"),
            ProjectModel(2, "Projet 2"),
            ProjectModel(3, "Projet 3"),
            ProjectModel(4, "Projet 4"),
            // Ajoutez d'autres exemples de projets selon vos besoins
        )

        // Créer des exemples de glossaires
        val glossaryList = listOf(
            GlossaryModel(1, "Glossaire 1"),
            GlossaryModel(2, "Glossaire 2"),
            GlossaryModel(3, "Glossaire 3"),
            GlossaryModel(4, "Glossaire 4"),
            // Ajoutez d'autres exemples de glossaires selon vos besoins
        )

        // Instancier un objet Home avec les exemples de projets et de glossaires
        val home = Home(glossaryList)
        home.mutableListProject = projectList

        // Appeler la méthode createMatrix() sur le contrôleur
        val matrix = home.controller.createMatrix()

        // Vérifier si la taille de la matrice est conforme aux attentes
        val expectedRowCount = (projectList.size + glossaryList.size) / home.controller.numberOfButtonPerRow
        assertEquals(expectedRowCount, matrix.size)

    }

    @Test
    fun addGlossaryTest() {
        // Créer un exemple de nom de glossaire
        val glossaryName = "Nouveau Glossaire"

        val controller = HomeController(Home(listOf()))

        controller.addGlossary(glossaryName)

        // Vérifier si le glossaire a été ajouté
        assertTrue(controller.parent.databaseModel.getGlossary().any { it.name == glossaryName })
    }

    @Test
    fun testChangeHomeView() {
        val homeController = HomeController(parent = Home(glossaryModelList = emptyList())) // Assuming an empty glossary model list for simplicity
        homeController.projectMode = true // Setting project mode to true for this test

        // Test when deleteMode is false
        var result = homeController.changeHomeView(deleteMode = false)
        assertEquals("Ajouter un projet", result["Add"], "Incorrect 'Add' button text for project mode")
        assertEquals("Supprimer un projet", result["Delete"], "Incorrect 'Delete' button text for project mode")

        // Test when deleteMode is true
        result = homeController.changeHomeView(deleteMode = true)
        assertEquals("Annuler", result["Add"], "Incorrect 'Add' button text in delete mode")
        assertEquals("Valider", result["Delete"], "Incorrect 'Delete' button text in delete mode")

        // Switching to glossary mode and testing again
        homeController.projectMode = false
        result = homeController.changeHomeView(deleteMode = false)
        assertEquals("Ajouter un glossaire", result["Add"], "Incorrect 'Add' button text for glossary mode")
        assertEquals("Supprimer un glossaire", result["Delete"], "Incorrect 'Delete' button text for glossary mode")
    }

    @Test
    fun `addProject should add project to database and update project list`() {
        // Arrange
        val parent = Home(glossaryModelList = emptyList()) // Replace with your actual parent class
        val homeController = HomeController(parent)
        val name = "TestProject"
        val fileDialog = FileDialog(Frame(), "Sélectionner des fichiers", FileDialog.LOAD)
        fileDialog.isMultipleMode = true
        fileDialog.isVisible = true
        val selectedFiles = mutableListOf<String>()
        fileDialog.files.forEach { file ->
            selectedFiles.add(file.absolutePath)
        }

        // Act
        homeController.addProject(name)

        // Assert
        val projects = parent.databaseModel.getProject()
        val addedProject = projects.find { it.name == name }
        assertTrue(addedProject != null, "Project '$name' not found in database after adding")
        assertEquals(projects.size, parent.projectList.size, "Project list size mismatch after adding project")
        println(parent.projectList)
        if (addedProject != null) {
            assertNotNull(parent.databaseModel.getProject(addedProject.id) , "Added project not found in project list")
        }
        assertTrue(!fileDialog.isVisible, "File dialog should not be visible after adding project")
    }

    @Test
    fun testDeleteProject() {
        // Mock the parent object
        val parent = Home(glossaryModelList = emptyList()) // Assuming an empty glossary model list for simplicity
        val homeController = HomeController(parent = parent)

        // Add some projects to the database for testing
        parent.databaseModel.addProject("Project1", emptyList())
        parent.databaseModel.addProject("Project2", emptyList())
        parent.databaseModel.addProject("Project3", emptyList())

        // Get the initial project list
        val initialProjects = parent.databaseModel.getProject()
        val initialProjectIds = initialProjects.map { it.id }.toMutableList()

        // Execute the method to delete projects
        homeController.deleteProject(initialProjectIds)

        // Verify that the projects are deleted from the database
        val remainingProjects = parent.databaseModel.getProject()
        assertEquals(0, remainingProjects.size, "Projects were not deleted from the database")

        // Verify that the project list is updated accordingly
        assertFalse(parent.projectList.isNotEmpty(), "Project list is not empty after deletion")
    }

    @Test
    fun testUpdateListGlossary() {
        // Mock the parent object
        val parent = Home(glossaryModelList = emptyList()) // Assuming an empty glossary model list for simplicity
        val homeController = HomeController(parent = parent)

        // Prepare a new list of glossary models
        val newGlossaryList = listOf(
            GlossaryModel(id = 1, name = "Glossary1"),
            GlossaryModel(id = 2, name = "Glossary2"),
            GlossaryModel(id = 3, name = "Glossary3")
        )

        // Execute the method to update the list of glossary models
        homeController.updateListGlossary(newGlossaryList)

        // Verify that the mutable list of glossary models is updated correctly
        assertEquals(newGlossaryList, parent.mutableListGlossary, "Mutable list of glossary models not updated correctly")

        // Verify that the glossary model list in the parent object is also updated correctly
        assertEquals(newGlossaryList, parent.glossaryModelList, "Glossary model list in parent object not updated correctly")
    }

    @Test
    fun testUpdateListProject() {
        // Mock the parent object
        val parent = Home(glossaryModelList = emptyList()) // Assuming an empty glossary model list for simplicity
        val homeController = HomeController(parent = parent)

        // Prepare a new list of project models
        val newProjectList = listOf(
            ProjectModel(id = 1, name = "Project1"),
            ProjectModel(id = 2, name = "Project2"),
            ProjectModel(id = 3, name = "Project3")
        )

        // Execute the method to update the list of project models
        homeController.updateListProject(newProjectList)

        // Verify that the mutable list of project models is updated correctly
        assertEquals(newProjectList, parent.mutableListProject, "Mutable list of project models not updated correctly")

        // Verify that the project model list in the parent object is also updated correctly
        assertEquals(newProjectList, parent.projectList, "Project model list in parent object not updated correctly")
    }

    @Test
    fun testNameAlreadyExistVerification_EmptyList() {
        val parent = mockParent(emptyList(), emptyList())
        val homeController = HomeController(parent = parent)

        val newName = homeController.nameAlreadyExistVerification("NewProject", "Project")

        assertEquals("NewProject", newName, "Name should remain unchanged when list is empty")
    }

    @Test
    fun testNameAlreadyExistVerification_NoSimilarNames() {
        val existingProjects = listOf(
            ProjectModel(1, "Project1"),
            ProjectModel(2, "Project2"),
            ProjectModel(3, "Project3")
        )
        val parent = mockParent(existingProjects, emptyList())
        val homeController = HomeController(parent = parent)

        val newName = homeController.nameAlreadyExistVerification("NewProject", "Project")

        assertEquals("NewProject", newName, "Name should remain unchanged when no similar names exist")
    }

    @Test
    fun testNameAlreadyExistVerification_SimilarNamesExist() {
        val existingProject = listOf(
            ProjectModel(1, "NewProject")
        )
        val parent = mockParent(existingProject, emptyList())
        val homeController = HomeController(parent = parent)
        homeController.deleteProject(parent.databaseModel.getProject().map { it.id }.toMutableList())
        homeController.addProject("NewProject")
        parent.databaseModel.getProject().forEach(::println)

        val newName = homeController.nameAlreadyExistVerification("NewProject", "Project")

        assertEquals("NewProject (1)", newName, "Name should be modified to avoid duplicates")

        homeController.deleteProject(parent.databaseModel.getProject().map { it.id }.toMutableList())
    }

    private fun mockParent(projectList: List<ProjectModel>, glossaryList: List<GlossaryModel>): Home {
        return Home(glossaryList).apply {
            mutableListProject = projectList.toMutableList()
        }
    }

    @Test
    fun testDeleteGlossary() {
        val initialGlossaryList = listOf(
            GlossaryModel(1, "Glossary1"),
            GlossaryModel(2, "Glossary2"),
            GlossaryModel(3, "Glossary3")
        )
        val parent = mockParent(emptyList(), initialGlossaryList)
        val homeController = HomeController(parent = parent)

        val initialGlossaryIds = initialGlossaryList.map { it.id }.toMutableList()
        val initialGlossaries = parent.databaseModel.getGlossary()
        homeController.deleteGlossary(initialGlossaryIds)

        val remainingGlossaries = initialGlossaries.filter { it.id in initialGlossaryIds }
        assertEquals(0, remainingGlossaries.size, "Glossaries were not deleted from the database")

    }

    @Test
    fun nameIsNotBlankTest() {
        val homeController = HomeController(parent = Home(glossaryModelList = emptyList()))
        val names = mutableStateOf(listOf("name1", "name2", "name3"))
        val result = homeController.isNameNotBlank(names)
        assertTrue(result, "Name should not be blank")
    }



}

