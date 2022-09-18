import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import java.awt.MouseInfo

class FolderView(private val model: Model, private val controller: Main, private val con: Connect) : VBox(), IView {
    val buttonView = HBox()
    val addFolderButton = Button("Folder")
    val addNoteButton = Button("Note")
    var folders = Accordion()
    var contextMenu = ContextMenu()
    val renameNoteMenuItem = MenuItem("Rename Folder")
    val deleteNoteMenuItem = MenuItem("Delete Folder")
    var currFolder : Folder? = null
    // Initial Thoughts: Folders will be Acordian widgets i.e drop down menus
    // Create list view for each new folder, the new notes become the children

    // master folder list in the model
    override fun updateView() {
        refreshFolders()
        for (folder in model.folders) {
            folder.folderPane.content = folder.notes
            folder.refreshNotes()
        }
        if (model.currentNote != null) { // set the folder containing the selected note to be expanded
            val folderName = model.currentNote?.parentFolder?.folderName
            val index = model.folders.indexOf(model.folders.find { it.folderName == folderName })
            folders.expandedPane = folders.panes[index]
        }
    }


    fun newFolder(folder: Folder)  {
        //var folder = Folder(name)
        folder.id = model.getAndUpdateNewFolderId()
        model.folderCount = model.folderCount + 1
        var pane: TitledPane = folder.returnFolderPane()
        pane.prefHeight = 40.0
        pane.prefWidth = 125.0
        pane.padding = Insets(3.0,3.0,3.0,3.0)
        pane.border = Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(0.0)))
        pane.contextMenu = contextMenu
        pane.addEventHandler(MouseEvent.MOUSE_CLICKED) {
            currFolder = folder
        }
        folders.panes.add(pane)
        model.folders.add(folder)
        con.addFolder(folder.id!!, folder.folderName, folder.noteCount)
        refreshFolders()
    }

    fun deleteFolder(folder: Folder) {
        var pane: TitledPane = folder.returnFolderPane()
        folders.panes.remove(pane)
        model.folders.remove(folder)
        model.folderCount = model.folderCount - 1
    }

    // only refreshes if a folder is deleted
    fun refreshFolders() {
//        folders.panes.clear()
        if (folders.panes.size > model.folders.size) {
            folders.panes.removeAt(model.folderIndexToDelete)
        }
        else if (folders.panes.size == model.folders.size) { // if no folders removed double check all the names
            for (i in 0 until model.folders.size) {
                folders.panes[i].text = model.folders[i].folderName
            }
        }
    }

    // this function actually isn't needed, its just used for some test cases in main
    fun addNotes(folder: Folder, note: Note) {
        folder.addNote(note)
        model.noteCount++
        folder.refreshNotes()
        folder.folderPane.content = folder.notes
        refreshFolders()
    }


    init {

        deleteNoteMenuItem.setOnAction {
            controller.deleteFolder(currFolder!!)

        }
        renameNoteMenuItem.setOnAction {
            controller.renameFolder(currFolder!!)
        }
        contextMenu.items.addAll(renameNoteMenuItem, deleteNoteMenuItem)
        addFolderButton.prefWidth = 60.0
        addNoteButton.prefWidth = 60.0
        addFolderButton.prefHeight = 20.0
        addNoteButton.prefHeight = 20.0

        addFolderButton.onAction = EventHandler { event: ActionEvent ->
            controller.addFolder()
        }

        addNoteButton.onAction = EventHandler { event: ActionEvent ->
            controller.addNote()
        }

        addNoteButton.font = Font.font("helvetica", 12.0)
        addFolderButton.font = Font.font("helvetica", 12.0)
        buttonView.spacing = 5.0
        buttonView.children.addAll(addFolderButton,addNoteButton)
        children.add(buttonView)
        padding = Insets(5.0, 5.0, 5.0, 5.0)
        border = Border(BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(1.0)))
        children.add(folders)
        model.addView(this)
    }
}
