import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger


class ToolbarView(private val model: Model, private val controller: Main) : VBox(), IView {
    private val menuBar = MenuBar()
    private val noteView = controller.noteView
    private val folderView = controller.folderView
    private val textArea = noteView.getTextArea()
    private var fontSize = 13
    val note : Note? = model.currentNote
    val lockNote = MenuItem("Lock Note")
    private var shortcut = KeyCombination.META_DOWN

    override fun updateView() {
        if(model.currentNote?.title != null) {
            if (model.currentNote?.isLocked!!) {
                lockNote.text = "Unlock Note"
            } else {
                lockNote.text = "Lock Note"
            }
        }


    }

    // Create NoteApp Menu and handle event
    fun NoteAppMenu(noteMenu: Menu){
        // noteapp menu items
        val aboutApp = MenuItem("About Note Taker")
        val preference = MenuItem("Preferences")
        val fileQuit = MenuItem("Quit")
        val separator = SeparatorMenuItem()

        // short-cut
        preference.accelerator = KeyCodeCombination(KeyCode.COMMA, shortcut)
        fileQuit.accelerator = KeyCodeCombination(KeyCode.Q, shortcut)

        aboutApp.setOnAction {
           aboutNoteView()
        }

        preference.setOnAction {
            PreferenceView(model)
        }
        fileQuit.setOnAction { Platform.exit() }

        noteMenu.items.addAll(aboutApp, preference, separator, fileQuit)
    }

    // Create File Menu and handle events
    fun FileMenu(fileMenu : Menu){
        val newNote = MenuItem("New Note")
        val newFolder = MenuItem("New Folder")
        val deleteNote = MenuItem("Delete Note")
        val closeNote = MenuItem("Close")
        val saveNote = MenuItem("Save As")
        val pinNote = MenuItem("Pin Note")
        //val lockNote = MenuItem("Lock Note")
        val duplicateNote = MenuItem("Duplicate Note")

        val separator = SeparatorMenuItem()
        val separator2 = SeparatorMenuItem()
        val separator3 = SeparatorMenuItem()

        // shortcut
        newNote.accelerator = KeyCodeCombination(KeyCode.N, shortcut)
        newFolder.accelerator = KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_DOWN, shortcut)
        deleteNote.accelerator = KeyCodeCombination(KeyCode.BACK_SPACE, shortcut)
        closeNote.accelerator = KeyCodeCombination(KeyCode.W, shortcut)
        pinNote.accelerator = KeyCodeCombination(KeyCode.P, shortcut)
        lockNote.accelerator = KeyCodeCombination(KeyCode.L, shortcut)
        duplicateNote.accelerator = KeyCodeCombination(KeyCode.D, shortcut)

        // event
        newNote.setOnAction { controller.addNote() }
        newFolder.setOnAction { controller.addFolder() }
        deleteNote.setOnAction {
            if (model.currentNote?.title != null) {
                controller.deleteNote(model.currentNote!!)
            }
        }
        closeNote.setOnAction { this.scene.window.hide() }

        saveNote.setOnAction {
            val fileChooser = FileChooser()
            //Set extension filter
            val extFilter = FileChooser.ExtensionFilter("Portable Document Format (PDF)", "*.pdf")
            val extFilter2 = FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt")
            fileChooser.extensionFilters.addAll(extFilter, extFilter2)

            //Show save file dialog
            val file = fileChooser.showSaveDialog(this.scene.window)
            fileChooser.initialFileName = note?.title

            if (file != null) {
                saveFile(textArea.text, file)
            }
        }
        pinNote.setOnAction {
            if(model.currentNote?.title != null){
                controller.pinNote(model.currentNote!!)
            }
        }

        lockNote.setOnAction {
            controller.lockNote(model.currentNote!!)
        }
        duplicateNote.setOnAction {
            if(model.currentNote?.title != null){
                controller.duplicateNote(model.currentNote!!)
            }
        }

        fileMenu.items.addAll(newNote, newFolder, deleteNote ,separator, closeNote, separator2, saveNote, separator3, pinNote, lockNote, duplicateNote)
    }

    // create Edit Menu and handle events
    fun EditMenu(editMenu: Menu){
        val undo = MenuItem("Undo")
        val redo = MenuItem("Redo")
        val copy = MenuItem("Copy")
        val cut = MenuItem("Cut")
        val paste = MenuItem("Paste")
        val delete = MenuItem("Delete")
        val selectAll = MenuItem("Select All")
       // val attachFile = MenuItem("Attach File")
        val find = Menu("Find")

        //val findInList = MenuItem("Note List Search")
        val findInNote = MenuItem("Find...")
        val findNext = MenuItem("Find Next")
        val findPrev = MenuItem("Find Previous")

        val separator = SeparatorMenuItem()
        val separator2 = SeparatorMenuItem()
        //val separator3 = SeparatorMenuItem()

        // shortcut
        undo.accelerator = KeyCodeCombination(KeyCode.Z, shortcut)
        if(isMacUser()){
            redo.accelerator = KeyCodeCombination(KeyCode.Z, KeyCombination.SHIFT_DOWN,shortcut)
        } else {
            redo.accelerator = KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN)
        }

        copy.accelerator = KeyCodeCombination(KeyCode.C, shortcut)
        cut.accelerator = KeyCodeCombination(KeyCode.X, shortcut)
        paste.accelerator = KeyCodeCombination(KeyCode.V, shortcut)
        delete.accelerator = KeyCodeCombination(KeyCode.BACK_SPACE)
        selectAll.accelerator = KeyCodeCombination(KeyCode.A, shortcut)
        //findInList.accelerator = KeyCodeCombination(KeyCode.F, KeyCombination.ALT_DOWN, shortcut)
        findInNote.accelerator = KeyCodeCombination(KeyCode.F, shortcut)
        findNext.accelerator = KeyCodeCombination(KeyCode.G, shortcut)
        findPrev.accelerator = KeyCodeCombination(KeyCode.G, KeyCombination.SHIFT_DOWN, shortcut)

        // event
        undo.setOnAction {
            textArea.undo()
            noteView.updateTextStyles()
        }
        redo.setOnAction {
            textArea.redo()
            noteView.updateTextStyles()
        }
        copy.setOnAction { textArea.copy() }
        cut.setOnAction {
            textArea.cut()
            noteView.updateTextStyles()
        }
        paste.setOnAction {
            textArea.paste()
            println("paste")
            noteView.updateTextStyles()
        }
        delete.setOnAction {
            textArea.deleteText(textArea.selection)
            noteView.updateTextStyles()
        }
        selectAll.setOnAction { textArea.selectAll() }

        findInNote.setOnAction { noteView.showSearchBar() }
        findNext.setOnAction { noteView.nextWord() }
        findPrev.setOnAction { noteView.prevWord() }

        // textarea default shortcut
        textArea.setOnKeyPressed(EventHandler<KeyEvent> { event ->
            if(KeyCodeCombination(KeyCode.Z, shortcut).match(event) ||
                KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN).match(event)){
                noteView.updateTextStyles()
                println("undoing")
            } else if(KeyCodeCombination(KeyCode.Z, KeyCombination.SHIFT_DOWN,shortcut).match(event) ||
                KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN).match(event)){
                noteView.updateTextStyles()
                println("redoing")
            } else if(KeyCodeCombination(KeyCode.X, shortcut).match(event) ||
                KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN).match(event)){
                noteView.updateTextStyles()
                println("cut")
            } else if(KeyCodeCombination(KeyCode.V, shortcut).match(event) ||
                KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN).match(event)){
                noteView.updateTextStyles()
                println("paste")
            } else if(KeyCodeCombination(KeyCode.BACK_SPACE).match(event) ||
                KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.SHORTCUT_DOWN).match(event)){
                noteView.updateTextStyles()
                println("delete")
            }
        })

        find.items.addAll(findInNote, findNext, findPrev)
        editMenu.items.addAll(undo, redo, separator, copy, cut, paste, delete, selectAll, separator2, find)

    }

    // Create view menu and handle events
    fun ViewMenu(viewMenu: Menu){
        //val sortFolder = MenuItem("Sort Folder By")
        //val sortNote = MenuItem("Sort Note By")
        val hideFolder = MenuItem("Hide Folders")
        val zoomIn = MenuItem("Zoom In")
        val zoomOut = MenuItem("Zoom Out")
        val originalSize = MenuItem("Original Size")
        val hideToolbar = MenuItem("Hide Toolbar")
        val fullScreen = MenuItem("Enter Full Screen")

        val separator2 = SeparatorMenuItem()
        val separator3 = SeparatorMenuItem()
        val separator4 = SeparatorMenuItem()

        // shortcut
        hideFolder.accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN, shortcut)
        zoomIn.accelerator = KeyCodeCombination(KeyCode.PERIOD, KeyCombination.SHIFT_DOWN, shortcut)
        zoomOut.accelerator = KeyCodeCombination(KeyCode.COMMA, KeyCombination.SHIFT_DOWN, shortcut)
        originalSize.accelerator = KeyCodeCombination(KeyCode.O, KeyCombination.SHIFT_DOWN, shortcut)
        fullScreen.accelerator = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, shortcut)

        // event handler
        var hidefolder = false
        hideFolder.setOnAction {
            if(!hidefolder){
                controller.hideFolderView() // hide folders
                hideFolder.text = "Show Folders"
                hidefolder = true;
            } else {
                controller.hideFolderView() // show folders
                hideFolder.text = "Hide Folders"
                hidefolder = false;
            }
        }

        zoomIn.setOnAction {
            if(zoomOut.isDisable && fontSize >= 7) zoomOut.isDisable = false
            if(fontSize <= 49){
                fontSize += 2
                textArea.style = ("-fx-font-size: " + fontSize + ";")
            }
            if(fontSize > 49){
                zoomIn.isDisable = true
            }

        }

        zoomOut.setOnAction {
            if(zoomIn.isDisable && fontSize < 49) zoomIn.isDisable = false
            if(fontSize > 9){
                fontSize -= 2
                textArea.style = ("-fx-font-size: " + fontSize + ";")
            }

            if(fontSize <= 9) zoomOut.isDisable = true
        }

        originalSize.setOnAction {
            if(zoomIn.isDisable) zoomIn.isDisable = false;
            if(zoomOut.isDisable) zoomOut.isDisable = false;
            textArea.style = ("-fx-font-size: 13;")
            fontSize = 13
        }

        var isHideToolbar = false
        hideToolbar.setOnAction {
            if(!isHideToolbar){
                noteView.hideTextEditingToolbar()
                isHideToolbar = true
                hideToolbar.text = "Show Toolbar"
            } else {
                noteView.hideTextEditingToolbar()
                isHideToolbar = false
                hideToolbar.text = "Hide Toolbar"
            }
        }

        fullScreen.setOnAction {
            var stage = (this.scene.window as Stage)
            if(!stage.isFullScreen){
                fullScreen.text = "Exit Full Screen"
                stage.isFullScreen = true
            } else {
                fullScreen.text = "Enter Full Screen"
                stage.isFullScreen = false
            }
        }


        viewMenu.items.addAll( hideFolder, separator2, zoomIn, zoomOut, originalSize, separator3, hideToolbar, separator4, fullScreen)
    }

    fun WindowMenu(windowMenu: Menu){
        val minimize = MenuItem("Minimize")
        val zoom = MenuItem("Zoom")

        // shortcut
        minimize.accelerator = KeyCodeCombination(KeyCode.M, shortcut)
        zoom.accelerator = KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN, shortcut)

        // event handler
        minimize.setOnAction {
            var stage = (this.scene.window as Stage)
            stage.isIconified = true

        }

        var isZoomed = false;
        var widthOriginal = 0.0
        var heightOriginal = 0.0
        var xOriginal = 0.0
        var yOriginal = 0.0
        zoom.setOnAction {
            var stage = (this.scene.window as Stage)
            if(!isZoomed){ // maximaze screen size
                widthOriginal = stage.width
                heightOriginal = stage.height
                xOriginal = stage.x
                yOriginal = stage.y
                stage.isMaximized = true
                isZoomed = true
            } else { // back to original size
                stage.width = widthOriginal
                stage.height = heightOriginal
                stage.x = xOriginal
                stage.y = yOriginal
                isZoomed = false
            }
        }

        windowMenu.items.addAll(minimize, zoom)
    }

    fun aboutNoteView(){
        val vBox = VBox()
        vBox.padding = Insets(10.0, 10.0, 10.0, 10.0)

        vBox.alignment = Pos.CENTER
        val label = Label("About Note Taker")
        VBox.setMargin(label, Insets(0.0, 0.0, 10.0, 0.0))
        label.font = Font("helvetica",18.0)
        val label2 = Label("Version 1.0.0")
        val label3 = Label("Copyright © Team 103")
        val label4 = Label("All rights reserved.")

        vBox.children.addAll(label, label2, label3, label4)

        val scene = Scene(vBox, 200.0, 150.0)
        val stage = Stage()
        stage.scene = scene
        stage.show()


    }

    fun isMacUser() : Boolean{
        val os = System.getProperty("os.name");
        return os != null && os.startsWith("Mac")
    }

    fun saveFile(content : String, file: File){
        try {
            if(file.extension == "txt"){
                val fileWriter: FileWriter
                fileWriter = FileWriter(file)
                fileWriter.write(content)
                fileWriter.close()
            } else {
                model.saveAsPDF(textArea, file)
            }
        } catch (ex: IOException) {
            Logger.getLogger(
               ToolbarView::class.java.name
            ).log(Level.SEVERE, null, ex)
        }
    }

    init {

         // for mac user
         val os = System.getProperty("os.name");
         if (os != null && os.startsWith("Mac")){
             shortcut = KeyCombination.META_DOWN
         } else {
             shortcut = KeyCombination.SHORTCUT_DOWN
         }


        // Note App menu
        val noteMenu = Menu("Note Taker")
        NoteAppMenu(noteMenu)

        val fileMenu = Menu("File")
        FileMenu(fileMenu)

        val editMenu = Menu("Edit")
        EditMenu(editMenu)

        val viewMenu = Menu("View")
        ViewMenu(viewMenu)

        val windowMenu = Menu("Window")
        WindowMenu(windowMenu)

        menuBar.menus.addAll(noteMenu, fileMenu, editMenu, viewMenu, windowMenu)

        children.addAll(menuBar)

        model.addView(this)
    }
}
