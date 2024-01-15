package Model

class GlossaryModel(override val id: Int, override val name: String) : Model() {
    /**Class permettant d'ajouter des Glossaires dans la BDD*/
    fun printName() {
        println("Nom du projet : $name")
    }
}