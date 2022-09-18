import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.layout.*
import javafx.scene.control.*

class RenameFolderView(
    val controller: Main,
    val model: Model,
    val folder: Folder
) : GridPane() {

    init {
        // setup main layout
        prefWidth = 150.0
        maxWidth = 150.0
        maxHeight = 80.0
        padding = Insets(10.0)
        background = Background(BackgroundFill(Color.LIGHTGRAY, null, null))
        hgap = 10.0
        vgap = 10.0

        var r = 0

        // change the form title depending on action
        val formTitle =
            if (folder.id == null)
                "Rename Folder"
            else
                "Rename ${folder.folderName}"

        val label = Label(formTitle)
        label.minWidth = 120.0
        add(label, 0, r, 2, 1)
        r++

        val title = TextField(folder.folderName)
        add(Label("Title"), 0, r)
        add(title, 1, r)
        r++

        val saveButton = Button("Save")
        saveButton.prefWidth = 100.0
        var folder = Folder(title.text, model)

        saveButton.setOnAction { _ ->
            var newFolderName = ""
            for (fold in model.folders) {
                if (fold.folderName == folder.folderName) {
                    if (model.folders.find { it.folderName == title.text } != null) { // check duplicate folder
                        newFolderName = "${title.text} (${fold.id})"
                    }
                    else {
                        newFolderName = title.text
                    }
                    fold.folderName = newFolderName
                }
            }
            folder.folderName = newFolderName
            controller.saveRenameFolder(folder)
        }

        val cancelButton = Button("Cancel")
        cancelButton.prefWidth = 100.0
        cancelButton.setOnAction { _ ->
            controller.saveFolder(null)
        }

        // button row has its own layout
        val buttonRow = HBox(saveButton, cancelButton)
        buttonRow.alignment = Pos.CENTER_RIGHT
        buttonRow.spacing = 10.0
        add(buttonRow, 0, r, 2, 1)
    }
}