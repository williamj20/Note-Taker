import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.SplitPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Stage

class Main : Application() {
    val model = Model()
    val con = Connect
    val folderView = FolderView(model, this, con)
    val noteView = NoteView(model, this, con)
    val toolBarView = ToolbarView(model, this)
    val dialogOverlay = BorderPane()
    val main = BorderPane()
    val splitPane = SplitPane()

    @Throws(Exception::class)
    override fun start(stage: Stage) {


        val root = StackPane()
        root.style = "-fx-font: 12px Arial"

//        // TESTING FOLDER VIEW STARTS
//        var folder = Folder("Untitled 0", model)
//        var folder2 = Folder("Untitled 1", model)
//        var folder3 = Folder("Untitled 2", model)
//
//        folderView.newFolder(folder)
        //folderView.newFolder(folder2)
        //folderView.newFolder(folder3)

//        var note1 = Note("cars", 0, folder, this, model)
       // var note2 = Note("boats", 2, folder2, this, model)
        //var note3 = Note("planes", 2, folder3, this, model)

//        folderView.addNotes(folder, note1)

//        var note1 = Note("cars", 0, folder, this)
//        var note2 = Note("boats", 2, folder2, this)
//        var note3 = Note("planes", 2, folder3, this)
//
//        folderView.addNotes(folder, note1)

        //folderView.addNotes(folder2, note2)
        //folderView.addNotes(folder3, note3)

        // TESTING FOLDER VIEW ENDS

        dialogOverlay.isVisible = false
        // add a margin
        dialogOverlay.padding = Insets(128.0)
        // semi-transparent to gray out main interface visible around dialog
        dialogOverlay.background = Background(BackgroundFill(Color(0.5, 0.5, 0.5, 0.5), null, null))


        root.children.addAll(main, dialogOverlay)


        splitPane.items.addAll(folderView, noteView)
        splitPane.setDividerPositions(0.17)
        folderView.maxWidth = 230.0
        folderView.minWidth = 0.0


        main.top = toolBarView
       // main.left = folderView
        main.center = splitPane

        con.main()

        val scene = Scene(root, 800.0, 600.0)
        stage.title = "Note Taker"
        stage.scene = scene
        stage.show()

        stage.setOnCloseRequest { Platform.exit() }
    }

    // Controller Functions

    fun addFolder() {
        //println("new Folder")
        var folder = Folder("Untitled", model)
        folder.id = null
        dialogOverlay.center = EditFolderView(this, model, folder)
        dialogOverlay.isVisible = true
        // for now just add random note title untitled but in future implement edit screen that allows u to set name
//        var folder = Folder("Untitled ${model.folderCount}")
//        folderView.newFolder(folder)

    }

    fun saveFolder(folder: Folder?) {
        //println("reached save")
        //println(folder?.folderName)
        dialogOverlay.children.clear()
        dialogOverlay.isVisible = false
        if (folder != null) {
            if (folder.id == null) {
                //model.addNote(folder)
                folderView.newFolder(folder)
                //println("${folder.folderName}")
               //println("FOLDER COUNT: ${model.folderCount}")
            }
            else
                //model.updateNote(note)
                println("update")
        }
    }

    // hide folder View
    fun hideFolderView(){
       if(splitPane.items.size == 2){
           splitPane.items.remove(folderView)
       } else {
           splitPane.items.add(0, folderView)
           splitPane.setDividerPositions(0.17)
       }
    }

    fun addNote() {
        dialogOverlay.center = CreateNoteView(model, this, con)
        dialogOverlay.isVisible = true
    }

    fun cancelDialogView() {
        dialogOverlay.children.clear()
        dialogOverlay.isVisible = false
    }

    fun saveNote(note: Note) {
        println("saving new note: " + note.title + " and id: " + note.id)
        model.saveNote(note)
        cancelDialogView()
    }

    fun deleteNote(note: Note) {
        dialogOverlay.center = ConfirmDeleteNote(this, note)
        dialogOverlay.isVisible = true
    }

    fun confirmDeleteNote(note: Note) {
        model.deleteNote(note)
        cancelDialogView()
    }

    fun pinNote(note: Note){
        model.pinNote(note)
    }

    fun duplicateNote(note: Note){
        var newNote = Note(note.title, model.getAndUpdateNewNoteId() ,note.parentFolder, this, model)
        newNote.body = note.body
        newNote.textStyles = note.textStyles.toList() as MutableList<String>
        model.duplicateNote(note, newNote)
    }

    fun renameNote(note: Note) {
        dialogOverlay.center = RenameNoteView(this, note)
        dialogOverlay.isVisible = true
    }

    fun deleteFolder(folder: Folder) {
        dialogOverlay.center = ConfirmDeleteFolder(this, folder)
        dialogOverlay.isVisible = true
    }

    fun confirmDeleteFolder(folder: Folder) {
        model.deleteFolder(folder)
        cancelDialogView()
    }

    fun renameFolder(folder: Folder) {
        dialogOverlay.center = RenameFolderView(this,model, folder)
        dialogOverlay.isVisible = true
    }

    fun saveRenameNote(note: Note, newName: String) {
        model.renameNote(note, newName)
        cancelDialogView()
    }

    fun saveRenameFolder(folder: Folder) {
        model.renameFolder(folder)
        cancelDialogView()
    }

    fun lockNote(note: Note){
        //noteView.getTextArea().isVisible = false
        if(model.password != "") {
            if (model.currentNote?.title != null) {
                if (!note.isLocked) {
                    note.isLocked = true
                    noteView.updateView()
                } else {
                    note.isLocked = false
                    LockView(model, this, note)
                }
                toolBarView.updateView()
            }
        }

    }

    fun unlockNote(note: Note){
        //note.isLocked = false
        toolBarView.updateView()
        noteView.updateView()
    }


}
