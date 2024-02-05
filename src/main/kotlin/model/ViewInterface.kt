package model

import controller.Controller
import view.Display

interface ViewInterface<AnyDisplay : Display, AnyController : Controller> {
    val viewIdentifier : Int
    val glossaryId : Int?
    val projectId : Int?
    val glossaryModelList : List<GlossaryModel>
    val databaseModel : DatabaseModel
    val display : AnyDisplay
    val controller : AnyController
}