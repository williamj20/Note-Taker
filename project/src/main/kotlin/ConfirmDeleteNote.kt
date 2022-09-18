import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.layout.*
import javafx.scene.control.*

class ConfirmDeleteNote(
    val controller: Main,
    val note: Note,
) : BorderPane() {
    private val confirmDeleteNoteLabel = Label("Delete Note: \"${note.title}\"?")
    private val buttonsHBox = HBox()
    private val confirmButton = Button("Confirm")
    private val cancelButton = Button("Cancel")

    init {
        maxWidth = 200.0
        maxHeight = 80.0
        padding = Insets(10.0)
        background = Background(BackgroundFill(Color.LIGHTGRAY, null, null))
        top = confirmDeleteNoteLabel
        bottom = buttonsHBox

        confirmButton.prefWidth = 100.0
        confirmButton.setOnAction { _ ->
            controller.confirmDeleteNote(note)
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
