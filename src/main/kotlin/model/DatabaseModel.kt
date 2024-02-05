package model

import java.io.File
import java.sql.Connection
import java.sql.DriverManager


class DatabaseModel {
    private var connection: Connection
    private val connectionString = "jdbc:sqlite:identifier.sqlite"

    init {
        // Initialisation de la base de donné en utilisant le nom de la base
        connection = DriverManager.getConnection(connectionString)
    }
    fun getConnection(){
        connection = DriverManager.getConnection(connectionString)
    }
    fun createDatabase(){
        try {
            // Lecture du fichier SQL
            val sql = File("poc.sqlite").readText()

            // Séparation des instructions SQL (en supposant qu'elles sont séparées par des points-virgules)
            val statements = sql.split(";")

            // Exécution de chaque instruction SQL
            for (statement in statements) {
                if (statement.trim().isNotEmpty()) {
                    connection.createStatement().use { stmt ->
                        stmt.execute(statement)
                    }
                }
            }
        } finally {
            // Fermeture de la connexion après utilisation
            updateConnection()
        }
    }
    fun addGlossary(name: String) {
        /**Permet d'ajouter des glossaires dans la BDD*/
        val connection = this.connection
        try {
            val statement = connection.prepareStatement("INSERT INTO Glossary (name) VALUES (?)")
            statement.setString(1, name)
            statement.execute()
            println("Glossaire ajouté")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
    }
    fun updateConnection(){
        connection.close();
        this.connection = DriverManager.getConnection(connectionString)
    }
    fun getGlossary():List<GlossaryModel> {
        /**Permet de récupéré une liste de glossaire*/
        val connection = this.connection
        val glossaryModels = mutableListOf<GlossaryModel>()

        // Requête afin de récupéré les Glossaires
        try {
            val selectQuery = "SELECT * FROM Glossary"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(selectQuery)

            while (resultSet.next()) {
                val glossaryId = resultSet.getInt("id_glossary")
                val glossaryName = resultSet.getString("name")

                val glossaryModel = GlossaryModel(glossaryId, glossaryName)
                glossaryModels.add(glossaryModel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
        return glossaryModels
    }

    fun getGlossary(glossaryId: Int?): GlossaryModel {
        /**Permet de récupéré un glossaire bien précis
         * Nécessite de passer son id*/
        val connection = this.connection
        var glossaryModel: GlossaryModel? = null


        try {
            val selectQuery = "SELECT * FROM Glossary where id_glossary = $glossaryId"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(selectQuery)

            while (resultSet.next()) {
                val selectedGlossaryId = resultSet.getInt("id_glossary")
                val selectedGlossaryName = resultSet.getString("name")
                glossaryModel = GlossaryModel(selectedGlossaryId, selectedGlossaryName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }

        //Faire attention de bien renvoyer une valeur non null
        //(Le type de retour n'accepte pas les nulls)
        return glossaryModel ?: throw NoSuchElementException("Glossary not found")
    }

    fun addProject(name: String, listPath : List<String>) {
        /**Permet d'ajouter des glossaires dans la BDD*/
        val connection = this.connection
        var projectId: Int? = null
        try {
            val addProjectQuery = connection.prepareStatement("INSERT INTO Project (name) VALUES (?)")
            addProjectQuery.setString(1, name)
            addProjectQuery.execute()
            println("Projet ajouté")

            val getIdQuery = connection.prepareStatement("SELECT id_project FROM Project WHERE name = ?")
            getIdQuery.setString(1, name)
            val result = getIdQuery.executeQuery()

            if (result.next()) {
                projectId = result.getInt("id_project")
                println("Projet ajouté avec succès. ID du projet: $projectId")
            } else {
                println("Échec de la récupération de l'ID du projet.")
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
        listPath.forEach { path ->
            addPath(projectId!!, path)
        }
    }
    fun addProject(name: String) {
        val connection = this.connection
        try {
            val statement = connection.prepareStatement("INSERT INTO Project (name) VALUES (?)")
            statement.setString(1, name)
            statement.execute()
            println("Project added")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
    }
    fun addPath(projectId: Int, path: String) {
        val connection = this.connection
        try {
            val statement = connection.prepareStatement("INSERT INTO Path (id_project,path) VALUES (?,?)")
            statement.setInt(1, projectId)
            statement.setString(2, path)
            statement.execute()
            println("Path added")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
    }
    fun deletePathsForProject(projectId: Int) {
        val connection = this.connection
        try {
            val statement = connection.prepareStatement("DELETE FROM Path WHERE id_project = ?")
            statement.setInt(1, projectId)
            statement.execute()
            println("Paths for Project $projectId deleted")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
    }
    fun deletePath(pathId: Int) {
        val connection = this.connection
        try {
            val statement = connection.prepareStatement("DELETE FROM Path WHERE id_path = ?")
            statement.setInt(1, pathId)
            statement.execute()
            println("Path $pathId deleted")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
    }
    fun deleteProject(projectId: Int) {
        val connection = this.connection
        try {
            val statement = connection.prepareStatement("DELETE FROM Project WHERE id_project = ?")
            statement.setInt(1, projectId)



            statement.execute()
            deletePathsForProject(projectId)
            println("Project $projectId deleted")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
    }
    fun getProject():List<ProjectModel> {
        /**Permet de récupéré une liste de glossaire*/
        val connection = this.connection
        val projectModels = mutableListOf<ProjectModel>()

        // Requête afin de récupéré les Glossaires
        try {
            val selectQuery = "SELECT * FROM Project"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(selectQuery)

            while (resultSet.next()) {
                val projectId = resultSet.getInt("id_project")
                val projectName = resultSet.getString("name")

                val projectModel = ProjectModel(projectId, projectName)
                projectModels.add(projectModel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
        return projectModels
    }

    fun getProject(projectId : Int): ProjectModel?{
        val connection = this.connection
        try {
            val statement = connection.prepareStatement("SELECT * FROM Project WHERE id_project = ?")
            statement.setInt(1, projectId)

            val resultSet = statement.executeQuery()
            if (resultSet.next()){
                val id = resultSet.getInt("id_project")
                val name = resultSet.getString("name")

                return ProjectModel(id,name)
            }

            statement.execute()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            updateConnection()
        }
        //Si le mot est introuvable retourne null
        return null
    }
    fun getPath(projectId : Int): List<String>{
        val connection = this.connection
        val listPath = mutableListOf<String>("")
        try {
            val statement = connection.prepareStatement("SELECT * FROM Path WHERE id_project = ?")
            statement.setInt(1, projectId)

            val resultSet = statement.executeQuery()
            while (resultSet.next()){
                val path = resultSet.getString("path")
                listPath.add(path)
            }

            statement.execute()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            updateConnection()
        }
        //Si le mot est introuvable retourne null
        return listPath
    }
    fun getGlossaryByName(name : String) : GlossaryModel? {
        val connection = this.connection
        val glossary : GlossaryModel
        try {
            val statement = connection.prepareStatement("SELECT * FROM Glossary WHERE name = ?")
            statement.setString(1, name)

            val resultSet = statement.executeQuery()
            if (resultSet.next()){
                val glossaryName = resultSet.getString("name")
                val idGlossary = resultSet.getInt("id_glossary")
                glossary = GlossaryModel(idGlossary, glossaryName)
                return glossary
            }
            statement.execute()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            updateConnection()
        }
        return null
    }
    fun updateWord(wordModel: WordModel, oldWord : WordModel) {
        /**Permet de modifier un mot déjà présent dans la BDD
         * Nécessite le mot*/
        val connection = this.connection

        try {
            val updateQuery = "UPDATE Word SET label = ?,definition=?, context=?, synonym=?, antonym=?, link_to=? WHERE label=? AND context = ?"
            val preparedStatement = connection.prepareStatement(updateQuery)
            preparedStatement.setString(1, wordModel.label)
            preparedStatement.setString(2, wordModel.def)
            preparedStatement.setString(3, wordModel.contextePrimaire)
            preparedStatement.setString(4, wordModel.synonyme)
            preparedStatement.setString(5, wordModel.antonyme)
            preparedStatement.setString(6, wordModel.lie)
            preparedStatement.setString(7, oldWord.label)
            preparedStatement.setString(8, oldWord.contextePrimaire)

            val rowsUpdated = preparedStatement.executeUpdate()

            if (rowsUpdated > 0) {
                println("Mise a jour du mot effectué")
            } else {
                println("Echec de la mise à jour")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
    }
    fun addWord(wordModel: WordModel) {
        val connection = this.connection

        try {
            val insertQuery = "INSERT INTO Word (id_glossary, label, definition, context, synonym, antonym, link_to) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)"
            val preparedStatement = connection.prepareStatement(insertQuery)
            preparedStatement.setInt(1, wordModel.glossaryId)
            preparedStatement.setString(2, wordModel.label)
            preparedStatement.setString(3, wordModel.def)
            preparedStatement.setString(4, wordModel.contextePrimaire)
            preparedStatement.setString(5, wordModel.synonyme)
            preparedStatement.setString(6, wordModel.antonyme)
            preparedStatement.setString(7, wordModel.lie)

            preparedStatement.executeUpdate()
            println("Mot ajouté")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }
    }
    fun getWords(glossaryId: Int): List<WordModel> {
        val connection = this.connection
        val wordModels = mutableListOf<WordModel>()

        try {
            val selectQuery = "SELECT * FROM Word WHERE id_glossary = ?"
            val preparedStatement = connection.prepareStatement(selectQuery)
            preparedStatement.setInt(1, glossaryId)
            val resultSet = preparedStatement.executeQuery()

            while (resultSet.next()) {
                val wordId = resultSet.getInt("id_word") //L'id du mot n'est pas présent dans le WordModel
                val label = resultSet.getString("label")
                val definition = resultSet.getString("definition")
                val context = resultSet.getString("context")
                val synonyme = resultSet.getString("synonym")
                val antonyme = resultSet.getString("antonym")
                val liaison = resultSet.getString("link_to")

                val wordModel = WordModel(glossaryId,wordId, label, definition,context, synonyme, antonyme, liaison)
                wordModels.add(wordModel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }

        return wordModels
    }

    fun getWordsAsMatrix(glossaryId: Int) : MutableList<List<String>>{
        val words = getWords(glossaryId)
        val wordMatrix : MutableList<List<String>> = mutableListOf()
        words.forEach {
            val wordAsList = it.wordToList()
            wordMatrix.add(wordAsList)
        }
        return wordMatrix
    }
    fun getWord(wordName: String,wordConcept : String, glossaryId: Int): WordModel? {
        val connection = this.connection

        try {
            val selectQuery = "SELECT * FROM Word WHERE label = ? AND context = ? AND id_glossary = ?"
            val preparedStatement = connection.prepareStatement(selectQuery)
            preparedStatement.setString(1, wordName)
            preparedStatement.setString(2, wordConcept)
            preparedStatement.setInt(3, glossaryId)
            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                val idWord = resultSet.getInt("id_word")
                val label = resultSet.getString("label")
                val definition = resultSet.getString("definition")
                val context = resultSet.getString("context")
                val synonyme = resultSet.getString("synonym")
                val antonyme = resultSet.getString("antonym")
                val liaison = resultSet.getString("link_to")

                //Retourne le mot si jamais il a été trouvé
                return WordModel(glossaryId,idWord, label, definition, context, synonyme, antonyme, liaison)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }

        return null // Si le mot n'est pas trouvé retourne null
    }
    fun deleteWord(glossaryId: Int, wordId: Int): Boolean {
        val connection = this.connection

        try {
            val deleteQuery = "DELETE FROM Word WHERE id_glossary = ? AND id_word = ?"
            val preparedStatement = connection.prepareStatement(deleteQuery)
            preparedStatement.setInt(1, glossaryId)
            preparedStatement.setInt(2, wordId)

            val rowsAffected = preparedStatement.executeUpdate()

            //Si jamais le mot est bien supprimé renvoie un booléen confirmant qu'il a bien été supprimé
            return rowsAffected > 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            updateConnection()
        }

        //Si le mot est introuvable retourne null
        return false
    }
    fun deleteGlossary(glossaryId: Int): Boolean {
        val connection = this.connection

        try {
            val deleteWordsQuery = "DELETE FROM Word WHERE id_glossary = ?"
            val preparedStatementWords = connection.prepareStatement(deleteWordsQuery)
            preparedStatementWords.setInt(1, glossaryId)

            // Delete words associated with the project
            preparedStatementWords.executeUpdate()

            // Now, delete the project itself
            val deleteGlossaryQuery = "DELETE FROM Glossary WHERE id_glossary = ?"
            val preparedStatementGlossary = connection.prepareStatement(deleteGlossaryQuery)
            preparedStatementGlossary.setInt(1, glossaryId)


            val rowsAffected = preparedStatementGlossary.executeUpdate()

            //Retourne un booleen si des lignes de la BDD ont été affecté
            return rowsAffected > 0
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the database retrieval error
        } finally {
            updateConnection()
        }

        // Si la suppression a échoué retourne false
        return false
    }
    fun closeConnection() {
        /**Fonction qui permet de fermer la connection à la base de donné*/
        connection.close()
    }
}
