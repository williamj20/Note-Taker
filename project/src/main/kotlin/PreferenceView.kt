import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.Separator
import javafx.scene.control.SplitMenuButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Stage

class PreferenceView(model: Model) {

    var hBox2 = HBox()
    var setPasswordButton = Button("")
    //var resetPasswordButton = Button("Reset Password")

    fun getSetPasswordBtn(): Button{
        return setPasswordButton
    }

    fun preferenceGuide(){
        var vBox = VBox(10.0)
        vBox.alignment = Pos.CENTER
        var title = Text("Change Preference in Note Taker")
        title.font = Font.font(15.0)
        var lockNote = Text("Locked Notes")
        var description = Text("Click Set Password to create a password")
        var description2 = Text("to lock/unlock note.")
        var description3 = Text("If you already set a password,")
        var description4 = Text("click change password to do it.")
        val separator = Separator()
        

        vBox.children.addAll(title, lockNote, separator, description, description2 ,description3, description4)
        val scene = Scene(vBox, 250.0, 200.0)
        val stage = Stage()
        stage.title = "Preference Guide"
        stage.scene = scene
        stage.show()
    }

    init{
        var vBox = VBox(10.0)
        vBox.padding = Insets(10.0, 10.0, 10.0, 10.0)

//        var hBox1 = HBox()
//       // hBox1.alignment = Pos.CENTER
//        var sortText = Text("Sort Notes By")
//        HBox.setMargin(sortText, Insets(5.0, 0.0, 0.0, 150.0))
//        var splitMenu = SplitMenuButton()
//        HBox.setMargin(splitMenu, Insets(0.0, 0.0, 0.0, 30.0))
//        splitMenu.text = "Sort Notes"
//        var created = MenuItem("Date Created")
//        var edited = MenuItem("Date Edited")
//        splitMenu.items.addAll(created, edited)
//        hBox1.children.addAll(sortText, splitMenu)

        var splitMenu = SplitMenuButton()
        HBox.setMargin(splitMenu, Insets(0.0, 0.0, 0.0, 30.0))

        //hBox2.alignment = Pos.CENTER
        var lockText = Text("Locked Notes")
        HBox.setMargin(lockText, Insets(5.0, 0.0, 0.0, 30.0))
        setPasswordButton.prefWidthProperty().bind(splitMenu.prefWidthProperty())
        HBox.setMargin(setPasswordButton, Insets(0.0, 0.0, 0.0, 28.0))
        //HBox.setMargin(resetPasswordButton, Insets(0.0, 0.0, 0.0, 10.0))

        // move to create password view
        if(model.password.length == 0){
            setPasswordButton.text = "Set Password"
            hBox2.children.addAll(lockText, setPasswordButton)
        } else {
            setPasswordButton.text = "Change Password"
            hBox2.children.addAll(lockText, setPasswordButton)
        }
        setPasswordButton.setOnAction {
            val passwordView = PasswordView(model, this)
            passwordView.setPasswordAction()
        }

        var hBox3 = HBox()
        hBox3.alignment = Pos.CENTER_RIGHT
        var helpbutton = Button()
        val img = Image("help.png")
        val view = ImageView(img)
        view.fitHeight = 20.0
        view.fitWidth = 20.0
        helpbutton.graphic = view
        helpbutton.style = "-fx-background-radius: 25;"
        hBox3.children.add(helpbutton)

        helpbutton.setOnAction {preferenceGuide() }

        val separator = Separator()

        vBox.children.addAll( hBox2, hBox3)
        val scene = Scene(vBox, 300.0, 100.0)
        val stage = Stage()
        stage.title = "Preference"
        stage.scene = scene
        stage.show()

    }
}