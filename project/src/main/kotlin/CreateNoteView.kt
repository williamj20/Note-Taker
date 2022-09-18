import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color

class CreateNoteView(private val model: Model, private val controller: Main, private val con: Connect) : BorderPane(), IView {
    private val createNoteContent = GridPane()
    private val createNoteLabel = Label("Create Note")
    private val folderLabel = Label("Folder:")
    private val folderChoiceBox = ChoiceBox<String>()
    private val noteTitleLabel = Label("Title:")
    private val noteTitleField = TextField()
    private val buttonsHBox = HBox()
    private val saveButton = Button("Save")
    private val cancelButton = Button("Cancel")
    private val statusMessage = Label("You must create a folder first!")

    override fun updateView() {
        val folders = model.folders
        folderChoiceBox.items.clear()
        if (folders.size == 0) {
            folderChoiceBox.isDisable = true
            saveButton.isDisable = true
            statusMessage.isVisible = true
        }
        else {
            folderChoiceBox.isDisable = false
            saveButton.isDisable = false
            statusMessage.isVisible = false
            for (folder in folders) {
                folderChoiceBox.items.add(folder.folderName)
            }
            folderChoiceBox.selectionModel.select(folders[0].folderName)
        }
    }

    init {
        maxWidth = 200.0
        maxHeight = 200.0
        padding = Insets(10.0)
        background = Background(BackgroundFill(Color.LIGHTGRAY, null, null))

        saveButton.prefWidth = 75.0
        cancelButton.prefWidth = 75.0
        buttonsHBox.spacing = 10.0
        buttonsHBox.alignment = Pos.CENTER
        buttonsHBox.children.addAll(saveButton, cancelButton)
        noteTitleField.prefWidth = 120.0
        folderChoiceBox.prefWidth = 120.0

        createNoteContent.hgap = 10.0
        createNoteContent.vgap = 20.0
        createNoteContent.add(folderLabel, 0, 0)
        createNoteContent.add(folderChoiceBox, 1, 0)
        createNoteContent.add(noteTitleLabel, 0, 1)
        createNoteContent.add(noteTitleField, 1, 1)
        createNoteContent.add(statusMessage, 0, 2, 2, 1)

        saveButton.setOnAction { _ ->
            val folderNameToBeSavedTo = folderChoiceBox.selectionModel.selectedItem
            val folder = model.folders.find { it.folderName == folderNameToBeSavedTo }
            val newNote = Note(noteTitleField.text, model.getAndUpdateNewNoteId(), folder, controller, model)
            con.addNote(newNote.id, newNote.title, folder?.id!!)
            controller.saveNote(newNote)
        }

        cancelButton.setOnAction { _ ->
            controller.cancelDialogView()
        }

        createNoteContent.alignment = Pos.CENTER

        top = createNoteLabel
        center = createNoteContent
        bottom = buttonsHBox

        model.addView(this)
    }
}
