import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage

class PasswordView(private val model: Model, val preferenceView: PreferenceView) {

    fun setPasswordAction() {
        val vBox = VBox(10.0)
        vBox.padding = Insets(10.0, 10.0, 10.0, 10.0)

        val isSetPassword = (preferenceView.setPasswordButton.text == "Set Password")

        val setPasswordLabel = Label("")
        if(isSetPassword){
            setPasswordLabel.text = "Create password"
        } else {
            setPasswordLabel.text = "Change password"
        }
        val hBox1 = HBox()
        hBox1.alignment = Pos.CENTER
        hBox1.children.addAll(setPasswordLabel)


        val hBox = HBox(10.0)
        hBox.alignment = Pos.CENTER
        val oldPasswordLabel = Label("Old Password:  ")
        val oldPasswordField = PasswordField()
        oldPasswordField.promptText = "Required"
        hBox.children.addAll(oldPasswordLabel, oldPasswordField)

        val hBox2 = HBox(10.0)
        hBox2.alignment = Pos.CENTER
        val passwordLabel = Label("")
        if(isSetPassword){
            passwordLabel.text = "Password: "
        } else {
            passwordLabel.text = "New password: "
        }
        val passwordField = PasswordField()
        passwordField.promptText = "Required"
        hBox2.children.addAll(passwordLabel, passwordField)

        val hBox3 = HBox(10.0)
        hBox3.alignment = Pos.CENTER
        val verifyLabel = Label("")
        val verifypasswordField = PasswordField()
        if(isSetPassword){
            verifyLabel.text = "Verify: "
            HBox.setMargin(verifypasswordField, Insets(0.0, 0.0, 0.0, 23.0))
        } else {
            verifyLabel.text = "Verify: "
            HBox.setMargin(verifypasswordField, Insets(0.0, 0.0, 0.0, 47.0))
        }


        verifypasswordField.promptText = "Required"
        hBox3.children.addAll(verifyLabel, verifypasswordField)

        val message = HBox()
        message.alignment = Pos.CENTER
        val notmatchLabel = Label("")
        notmatchLabel.textFill = Color.RED
        notmatchLabel.isVisible = false
        message.children.add(notmatchLabel)

        val hBox4 = HBox()
        hBox4.alignment = Pos.CENTER_RIGHT
        val setButton = Button("Set Password")
        hBox4.children.add(setButton)

        val scene = Scene(vBox, 350.0, 210.0)

        if(isSetPassword){
            vBox.children.addAll(hBox1, hBox2, hBox3, message,hBox4)
        } else {
            vBox.children.addAll(hBox1, hBox, hBox2, hBox3, message,hBox4)

        }

        val stage = Stage()
        stage.title = "Set Password"
        stage.scene = scene
        stage.show()

        setButton.setOnAction {
            if (oldPasswordField.getText() != model.password){
                notmatchLabel.isVisible = true
                notmatchLabel.text = "Incorrect password"
            } else if (passwordField.getText() != verifypasswordField.getText()) {
                notmatchLabel.isVisible = true
                notmatchLabel.text = "The passwords do not match"
            } else if (passwordField.getText().length == 0){
                notmatchLabel.isVisible = true
                notmatchLabel.text = "Enter a password"
            } else {
                model.password = passwordField.getText()
                notmatchLabel.isVisible = false
                preferenceView.setPasswordButton.text = "Change Password"
                /*if(isSetPassword){
                    preferenceView.hBox2.children.add(preferenceView.resetPasswordButton)
                }*/
                stage.close()
            }
        }
    }




    init{
    }

}