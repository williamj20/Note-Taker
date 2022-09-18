import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import java.text.SimpleDateFormat
import java.util.*


class Note(var title: String, val id: Int, var parentFolder: Folder?, controller: Main, model: Model) {
    var body: String = ""
    var dateModified: String = SimpleDateFormat("MMMM dd, yyyy hh:mm a").format(Calendar.getInstance().time)
    var contextMenu = ContextMenu()
    val deleteNoteMenuItem = MenuItem("Delete Note")
    val renameNoteMenuItem = MenuItem("Rename Note")
    val pinNoteMenuItem = MenuItem("Pin Note")
    val duplicateNoteMenuItem = MenuItem("Duplicate Note")
    val lockNoteMenuItem = MenuItem("Lock Note")
    var moveNoteMenuItem = MenuItem("Move To")

    var textStyles: MutableList<String> = mutableListOf()
    var isLocked = false

    fun setAsNoteSelected(event: MouseEvent, model: Model?) {
        println("note selected")
        if (event.clickCount == 1 && event.button != MouseButton.SECONDARY) {
            model?.setAsSelectedNote(this)
        }
    }

    init {
        println("date created: $dateModified")

        deleteNoteMenuItem.setOnAction {
            if (parentFolder != null) {
                controller.deleteNote(this)
            }
        }
        renameNoteMenuItem.setOnAction {
            controller.renameNote(this)
        }

        pinNoteMenuItem.setOnAction {
            controller.pinNote(this)
        }

        duplicateNoteMenuItem.setOnAction {
            controller.duplicateNote(this)
        }

        lockNoteMenuItem.setOnAction {
            controller.lockNote(this)
            println(this.isLocked)
            if(this.isLocked){
                lockNoteMenuItem.text = "Unlock Note"
            } else {
                lockNoteMenuItem.text = "Lock Note"
            }
        }

        moveNoteMenuItem.setOnAction{
            MoveFolderView(model, this)
        }


        val separator = SeparatorMenuItem()

        contextMenu.items.addAll(deleteNoteMenuItem, separator ,renameNoteMenuItem, pinNoteMenuItem, lockNoteMenuItem ,duplicateNoteMenuItem, moveNoteMenuItem)
    }
}
