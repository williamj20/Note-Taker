import javafx.scene.control.Button
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToolBar
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority


class TextEditingToolbarView(private val model: Model, private val controller: Main) : ToolBar(), IView {

    val boldButton = ToggleButton()
    val italicsButton = ToggleButton()
    val underlineButton = ToggleButton()
    val PDFButton = Button("")

    override fun updateView() {
        println("View: update text editing toolbar")
        boldButton.isSelected = model.boldToggle
        italicsButton.isSelected = model.italicToggle
        underlineButton.isSelected = model.underlineToggle
    }


    init {
        boldButton.graphic = ImageView(Image("boldIcon.png", 15.0, 15.0, false, true))
        italicsButton.graphic = ImageView(Image("italicsIcon.png", 15.0, 15.0, false, true))
        underlineButton.graphic = ImageView(Image("underlineIcon.png", 15.0, 15.0, false, true))
        this.style = "-fx-background-color: #EEEEEE; -fx-border-width: 2; -fx-border-color: darkgray; -fx-border-style: hidden hidden solid hidden;"

//        boldButton.style = "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
//        boldButton.isFocusTraversable = false
//        italicsButton.isFocusTraversable = false
//        underlineButton.isFocusTraversable = false

        val img = Image("export2.png")
        val pdfView = ImageView(img)
        PDFButton.graphic = pdfView
        pdfView.fitHeight = 15.0
        pdfView.fitWidth = 15.0
        PDFButton.style = "-fx-background-color: transparent;"

        val spacer = Pane()
        HBox.setHgrow(spacer, Priority.ALWAYS)
        spacer.setMinSize(10.0, 1.0)

        this.items.addAll(boldButton, italicsButton, underlineButton, spacer,PDFButton)

        // register with the model when we're ready to start receiving data
        model.addView(this)
    }
}