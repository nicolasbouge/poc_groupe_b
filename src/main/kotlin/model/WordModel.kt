package model

class WordModel (val glossaryId : Int, val idWord : Int, val label : String, val def : String, val contextePrimaire : String, val synonyme : String, val antonyme : String, val lie : String) {
    fun wordToList(): List<String> {
        return listOf(label,def,contextePrimaire,synonyme,antonyme,lie)
    }
}