import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.layout.*
import javafx.scene.control.*

class ConfirmDeleteFolder(
    val controller: Main,
    val folder: Folder,
) : BorderPane() {
    private val confirmDeleteFolderLabel = Label("Delete Folder: \"${folder.folderName}\"?")
    private val buttonsHBox = HBox()
    private val confirmButton = Button("Confirm")
    private val cancelButton = Button("Cancel")

    init {
        maxWidth = 200.0
        maxHeight = 80.0
        padding = Insets(10.0)
        background = Background(BackgroundFill(Color.LIGHTGRAY, null, null))
        top = confirmDeleteFolderLabel
        bottom = buttonsHBox

        confirmButton.prefWidth = 100.0
        confirmButton.setOnAction { _ ->
            controller.confirmDeleteFolder(folder)
        }

        cancelButton.prefWidth = 100.0
        cancelButton.setOnAction { _ -> controller.cancelDialogView()
        }

        // button row has its own layout
        buttonsHBox.children.addAll(confirmButton, cancelButton)
        buttonsHBox.alignment = Pos.CENTER_RIGHT
        buttonsHBox.spacing = 10.0
    }
}
