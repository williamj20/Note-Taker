import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage

class LockView(private val model: Model, private val controller: Main, private val note: Note) {
    init{
        val vBox = VBox(10.0)
        vBox.padding = Insets(10.0, 10.0, 10.0, 10.0)

        vBox.alignment = Pos.CENTER
        val label = Label("Enter a password to view note")
        val passwordField = PasswordField()
        passwordField.promptText = "Enter a password"
        val okButton = Button("OK")
        val message = Label("Incorrect password")
        message.textFill = Color.RED
        message.isVisible = false
        vBox.children.addAll(label, passwordField, message, okButton)

        val scene = Scene(vBox, 200.0, 150.0)
        val stage = Stage()
        stage.title = "Unlock Note"
        stage.scene = scene
        stage.show()

        okButton.setOnAction {
            if(passwordField.getText() != model.password) {
                message.isVisible = true
            } else {
                message.isVisible = false
                controller.unlockNote(note)
                stage.close()
            }
        }

    }
}