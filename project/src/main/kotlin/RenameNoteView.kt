import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.layout.*
import javafx.scene.control.*

class RenameNoteView(
    val controller: Main,
    val note: Note,
) : GridPane() {
    private val renameNoteLabel = Label("Rename Note")
    private val noteTitleLabel = Label("Title:")
    private val noteTitleField = TextField()
    private val buttonsHBox = HBox()
    private val saveButton = Button("Save")
    private val cancelButton = Button("Cancel")

    init {
        prefWidth = 150.0
        maxWidth = 150.0
        maxHeight = 80.0
        padding = Insets(10.0)
        background = Background(BackgroundFill(Color.LIGHTGRAY, null, null))
        hgap = 10.0
        vgap = 10.0


        add(renameNoteLabel, 0, 0, 2, 1)

        noteTitleField.text = note.title
        add(noteTitleLabel, 0, 1)
        add(noteTitleField, 1, 1)

        saveButton.prefWidth = 100.0
        saveButton.setOnAction { _ ->
            controller.saveRenameNote(note, noteTitleField.text)
        }

        cancelButton.prefWidth = 100.0
        cancelButton.setOnAction { _ -> controller.cancelDialogView()
        }

        // button row has its own layout
        buttonsHBox.children.addAll(saveButton, cancelButton)
        buttonsHBox.alignment = Pos.CENTER_RIGHT
        buttonsHBox.spacing = 10.0
        add(buttonsHBox, 0, 3, 2, 1)
    }
}