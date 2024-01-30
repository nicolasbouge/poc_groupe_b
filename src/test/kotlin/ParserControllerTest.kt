package main

import Controller.ParserController
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.File


class ParserControllerTest {

    @Test
    fun extractTextFromFoldershouldreturnalistoftextfromfilesinthegivenfolder() {
        val folderPath = "src/test/resources/test_folder"
        val file1 = File("$folderPath/file1.py")


        val file2 = File("$folderPath/file2.py")


        val listPath = listOf(file1.absolutePath, file2.absolutePath)

        val result = ParserController.extractTextFromFolder(listPath)

        assertEquals(
            listOf(
                listOf("Voiture", "1", "variable", "file1"),
                listOf("Moto", "1", "variable", "file2")
            ),
            result
        )    }

    @Test
    fun extractTextFromFileshouldreturnalistoftextfromthegivenfile() {
        val folderPath = "src/test/resources/test_folder"

        val file = File("$folderPath/file1.py")

        val result = ParserController.extractTextFromFile(file)

        assertEquals(listOf(listOf("Voiture", "1", "variable", "file1")), result)
    }

    @Test
    fun `deleteCommentInPythonFile should remove comments from the given file`() {
        val folderPath = "src/test/resources/test_folder"

        val file = File("$folderPath/file1.py")

        val result = ParserController.deleteCommentInPythonFile(file)

        assertEquals(
            "Text from file 1\nVoiture = 10",
            result.readText().trim()
        )
    }

    @Test
    fun `deleteDocstringInPythonFile should remove docstrings from the given file`() {
        val folderPath = "src/test/resources/test_folder"

        val file = File("$folderPath/file2.py")

        val result = ParserController.deleteDocstringInPythonFile(file)
        val expectedContent = Regex("Text from file 2[\\r\\n]+Moto = 10")

        assertTrue(expectedContent.matches(result.readText().trim()))
    }


    /*
    @Test
    fun shouldreturnalistofclassesvariablesandfunctionsfromthegivenfile() {
        val file = File("test_file.py")
        file.writeText(
            """
            class MyClass:
                variable1 = 10
                
                def my_function(self):
                    pass
                
                def another_function(self):
                    pass
                
            variable2 = "Hello, World!"
            """
        )

        val result = ParserController.parsePythonFile(file)

        val expectedClasses = listOf(listOf("MyClass", "1", "classe", "test_file.py"))
        val expectedVariables = listOf(listOf("variable1", "1", "variable", "test_file.py"))
        val expectedFunctions = listOf(
            listOf("my_function", "1", "fonction", "test_file.py"),
            listOf("another_function", "1", "fonction", "test_file.py")
        )

        assertEquals(expectedClasses + expectedVariables + expectedFunctions, result)
    }

    @Test
    fun `findDuplicate should correctly count occurrences and return a list without duplicates`() {
        val listTerms = mutableListOf(
            mutableListOf("term1", "1", "type1", "file1"),
            mutableListOf("term2", "1", "type2", "file1"),
            mutableListOf("term1", "1", "type1", "file1"),
            mutableListOf("term3", "1", "type3", "file1")
        )

        val result = ParserController.findDuplicate(listTerms, "type1")

        val expected = mutableListOf(
            mutableListOf("term1", "2", "type1", "file1"),
            mutableListOf("term2", "1", "type2", "file1"),
            mutableListOf("term3", "1", "type3", "file1")
        )

        assertEquals(expected, result)
    }

    @Test
    fun `removeDuplicates should remove duplicate lists from the given list of lists`() {
        val list = mutableListOf(
            mutableListOf("term1", "1", "type1", "file1"),
            mutableListOf("term2", "1", "type2", "file1"),
            mutableListOf("term1", "1", "type1", "file1"),
            mutableListOf("term3", "1", "type3", "file1")
        )

        val result = ParserController.removeDuplicates(list)

        val expected = mutableListOf(
            mutableListOf("term1", "1", "type1", "file1"),
            mutableListOf("term2", "1", "type2", "file1"),
            mutableListOf("term3", "1", "type3", "file1")
        )

        assertEquals(expected, result)
    }

    */
}