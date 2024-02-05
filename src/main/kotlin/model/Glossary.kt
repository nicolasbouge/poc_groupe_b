package model

import controller.GlossaryController
import view.GlossaryDisplay

class Glossary(
    override val glossaryId: Int,
    override val glossaryModelList: List<GlossaryModel>,
) : ViewInterface<GlossaryDisplay, GlossaryController> {
    override val projectId: Int? = null
    override val viewIdentifier: Int = 1
    override val controller: GlossaryController = GlossaryController(this)
    override val display: GlossaryDisplay = GlossaryDisplay(this)
    override val databaseModel = DatabaseModel()
}