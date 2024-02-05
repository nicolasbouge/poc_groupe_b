package controller


import java.io.File

object ParserController {
    fun extractTextFromFolder(listPath : List<String>): MutableList<List<String>> {
        val textList = mutableListOf<List<String>>()
        listPath.forEach {path ->
            val file = File(path)
            if (file.isFile){
                textList.addAll(extractTextFromFile(file))
            }
        }
        return textList
    }

    // Replace this function with your own text extraction logic
    fun extractTextFromFile(file: File): MutableList<MutableList<String>> {
        var newfile = deleteCommentInPythonFile(file)
        newfile = deleteDocstringInPythonFile(newfile)
        return parsePythonFile(newfile)
    }
    fun deleteCommentInPythonFile(file : File): File {
        val lines = file.readLines()
        val withoutCommentLines = mutableListOf<String>()

        for (line in lines) {
            val withoutCommentline = line.split("#")[0].trim()
            if (withoutCommentline.isNotEmpty()) {
                withoutCommentLines.add(withoutCommentline)
            }
        }

        val withoutCommentFile = File(file.nameWithoutExtension)
        withoutCommentFile.writeText(withoutCommentLines.joinToString("\n"))
        return withoutCommentFile
    }
    fun deleteDocstringInPythonFile(file : File): File {

        var text = file.readText()
        val docstringPattern = Regex("(\"\"\"(.*?)\"\"\")", RegexOption.DOT_MATCHES_ALL)
        val matches = docstringPattern.findAll(text)

        for (match in matches){
            text = text.replace(match.value, "")
        }
        val withoutDocstringFile = File(file.nameWithoutExtension)
        withoutDocstringFile.writeText(text)
        return withoutDocstringFile
    }
    private fun parsePythonFile(file: File): MutableList<MutableList<String>> {
        val fileContent = file.readLines()

        val regexClass = Regex("\\bclass\\s+([a-zA-Z_][a-zA-Z0-9_]*)")
        val regexVariable = Regex("^([a-zA-Z][a-zA-Z0-9_]*)\\s*(= |:\\s*[a-zA-Z][a-zA-Z0-9]* | [+]=)")
        val regexFunction = Regex("def\\s*([a-zA-Z]+[a-zA-Z0-9_]*)")

        var classes: MutableList<MutableList<String>> = mutableListOf()
        var variables: MutableList<MutableList<String>> = mutableListOf()
        var functions: MutableList<MutableList<String>> = mutableListOf()
        val parameters: MutableList<MutableList<String>> = mutableListOf()

        fileContent.forEach { line ->
            when {
                regexClass.find(line) != null -> {
                    val newWord = mutableListOf(
                        regexClass.find(line)?.groupValues?.get(1) ?: "",
                        "1",
                        "classe",
                        file.name
                    )
                    classes.add(newWord)
                }
                regexVariable.find(line) != null -> {
                    val newWord = mutableListOf(
                        regexVariable.find(line)?.groupValues?.get(1) ?: "",
                        "1",
                        "variable",
                        file.name
                    )
                    variables.add(newWord)
                }
                regexFunction.find(line) != null -> {
                    val newFunction = mutableListOf(
                        regexFunction.find(line)?.groupValues?.get(1) ?: "",
                        "1",
                        "fonction",
                        file.name
                    )
                    functions.add(newFunction)
                }
            }
        }

        classes = findDuplicate(classes)
        variables = findDuplicate(variables)
        functions = findDuplicate(functions)


        return (classes + variables + parameters + functions).toMutableList()
    }
    private fun findDuplicate(listTerms : MutableList<MutableList<String>>): MutableList<MutableList<String>> {
        /**Fonction qui permet d'augmenter le nombre d'occurence en fonction du nombre
         * de répétition d'un mot dans la liste*/
        val occurence = mutableMapOf<String, Int>()
        val newListOfTerms : MutableList<MutableList<String>> = mutableListOf()
        if (listTerms.isNotEmpty()){
            var size = listTerms.size - 1
            listTerms.forEach { terme ->
                occurence[terme[0]] = (occurence[terme[0]]?.plus(1)) ?: 1
                if (occurence[terme[0]]!! > 1){
                    size -= 1
                }
            }
            val newList = removeDuplicates(listTerms)
            for (i in 0..size){
                newListOfTerms.add(mutableListOf(
                    newList[i][0],
                    occurence[newList[i][0]].toString(),
                    newList[i][2],
                    newList[i][3]
                ))
            }
            return newListOfTerms
        }
        return listTerms
    }
    private fun removeDuplicates(listOfLists: MutableList<MutableList<String>>): MutableList<MutableList<String>> {
        val uniqueLists = mutableListOf<MutableList<String>>()
        val seen = mutableSetOf<List<String>>()

        for (list in listOfLists) {
            if (seen.add(list)) {
                uniqueLists.add(list.toMutableList())
            }
        }

        return uniqueLists
    }
}