import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

class Folder {
    var folderName: String = "Untitled"
    var noteCount: Int = 0
    private var thisModel: Model? = null
    private var thiscontroller: Main? = null

    var id: Int? = null
    var folderPane = TitledPane()
    val noteslist: ArrayList<Note> = ArrayList()
    var notes = VBox()

    var contextMenu = ContextMenu()
    val newFolderMenuItem = MenuItem("New Folder")

    constructor(name: String, model: Model) {
        folderName = name
        folderPane.text = name
        folderPane.text.padStart(10, ' ')
        folderPane.font = Font.font("Helvetica", 12.0)
        thisModel = model
//        var test = Label("TEST")
//        test.font = Font.font("Helvetica", 12.0)
//        folderPane.content = test
    }


    fun returnFolderPane(): TitledPane {
        folderPane.text = this.folderName
        return folderPane
    }

    fun addNote(note: Note) {
        noteslist.add(note)
        noteCount += 1
    }

    fun duplicationNote(note: Note, newNote: Note){
        var idx = noteslist.indexOf(note)
        noteslist.add(idx+1, newNote)
        noteCount += 1
    }

    fun deleteNote(noteID: Int) {
        for (note in noteslist) {
            if (noteID == note.id) {
                noteslist.remove(note)
                noteCount -= 1
                break
            }
        }
    }

    fun refreshNotes() {
        notes.children.clear()
        for (note in noteslist) {
            var title = Label(note.title)
            title.font = Font.font("Helvetica", 12.0)
            title.padding = Insets(1.5,5.0,1.5,20.0)
            title.setOnMouseClicked { event ->
                note.setAsNoteSelected(event, thisModel)
            }
            if (note.id == thisModel?.currentNoteId) {
                title.style = ("-fx-font-weight: bold;")
            }
            notes.children.add(title)
            title.contextMenu = note.contextMenu
        }
    }


    fun renameFolder(newName: String) {
        folderName = newName
    }

    fun pinNote(note: Note){
        noteslist.remove(note)
        noteslist.add(0, note)
    }

    init {
        notes.padding = Insets(5.0,5.0,5.0,5.0)
        notes.border = Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(0.0)))
    }
}
