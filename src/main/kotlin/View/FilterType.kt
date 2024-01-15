enum class BoardValues(val listColumnName : List<String>){
    GLOBAL(listOf("Terme","Définition","Contexte", "Synonyme", "Antonyme", "Lié à")),
    CONTEXTE(listOf("Terme","Contexte")),
    PARSERGLOBAL(listOf("Terme", "Occurence","Concept", "Fichier")),
    PARSERFILE(listOf("Terme", "Occurence"))
}