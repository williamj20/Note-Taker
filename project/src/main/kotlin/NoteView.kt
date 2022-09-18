import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.FileChooser
import org.fxmisc.richtext.InlineCssTextArea



class NoteView(private val model: Model, private val controller: Main, private val con: Connect) : VBox(), IView {
    private var noteTextArea = InlineCssTextArea("")
    private var textEditingToolbar = TextEditingToolbarView(model, controller)
    private var dateModified = Label("")
    private var updateButtons = false
    private var caretIndex = 0

    private val searchBar = HBox()
    private val findTextField = TextField()
    private var wordCount = 0
    private var countLabel = Label("")
    private var idx = 0

    val boldShortcutWindows: KeyCombination = KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN)
    val italicsShortcutWindows: KeyCombination = KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN)
    val underlineShortcutWindows: KeyCombination = KeyCodeCombination(KeyCode.U, KeyCombination.SHORTCUT_DOWN)
    val boldShortcutMac: KeyCombination = KeyCodeCombination(KeyCode.B, KeyCombination.META_DOWN)
    val italicsShortcutMac: KeyCombination = KeyCodeCombination(KeyCode.I, KeyCombination.META_DOWN)
    val underlineShortcutMac: KeyCombination = KeyCodeCombination(KeyCode.U, KeyCombination.META_DOWN)

    override fun updateView() {
        if (model.currentNote == null) {
            noteTextArea.clear()
            noteTextArea.isDisable = true
            dateModified.text = ""
            textEditingToolbar.PDFButton.isDisable = true
            return
        }
        textEditingToolbar.PDFButton.isDisable = false
        noteTextArea.isDisable = false
        if(model.currentNote?.isLocked!!){
            noteTextArea.isVisible = false
        } else {
            noteTextArea.isVisible = true
        }
        dateModified.text = "Last Modified: ${model.currentNote?.dateModified}"
        if (model.changedNote == true) {
            noteTextArea.clear()
            noteTextArea.replaceText(0, noteTextArea.text.length, model.currentNote?.body)
            model.noteChanged()
        }
        for (i in 0 until model.currentNote?.textStyles!!.size) {
            noteTextArea.setStyle(i, i+1, model.currentNote?.textStyles!![i])
        }
        print(noteTextArea.text)
        print(model.currentNote?.textStyles!!)
    }

    fun getTextArea(): InlineCssTextArea {
        return this.noteTextArea
    }

    fun hideTextEditingToolbar() {
        if(children.size == 3){
            children.remove(textEditingToolbar)
        } else {
            children.add(0, textEditingToolbar)
        }
    }

    fun showSearchBar(){
        if(!children.contains(searchBar)) {
            if (!children.contains(textEditingToolbar)) {
                children.add(0, searchBar)
            } else {
                children.add(1, searchBar)
            }
        }
        findTextField.requestFocus()
    }

    fun updateTextStyles() {
        var textStyles = mutableListOf<String>()
        for (i in 0 until noteTextArea.text.length) {
            textStyles.add(noteTextArea.getStyleOfChar(i))
        }
        model.updateTextStyles(textStyles)
    }

    fun getSelectionRange(selectionString: String): MutableList<Int> {
        var start = ""
        var end = ""
        var flag = false
        for (i in selectionString.indices) {
            if (selectionString[i] == ',' || selectionString[i] == ' ') {
                flag = true
                continue
            }
            if (!flag) {
                start += selectionString[i]
            } else {
                end += selectionString[i]
            }
        }
        return mutableListOf(start.toInt(), end.toInt())
    }

    fun searchWord() {
        wordCount = 0
        if(findTextField.text != null && findTextField.text != ""){
            val str = noteTextArea.text.lowercase()
            val textField = findTextField.text.lowercase()
            idx = noteTextArea.text.indexOf(findTextField.text, idx, true)
            var length = findTextField.length

            if(idx != -1){
                noteTextArea.selectRange(idx, idx + length)
            }

            var idx2 = str.indexOf(textField)
            while(idx2 != -1){
                wordCount++
                idx2 = str.indexOf(textField, idx2 +1)
            }
        }
        countLabel.text = wordCount.toString()
    }

    fun nextWord(){
        if(findTextField.text != null && findTextField.text != ""){
            idx = noteTextArea.text.indexOf(findTextField.text, idx+1, true)
            if(idx == -1){
                idx = noteTextArea.text.indexOf(findTextField.text, 0, true)
            }
            var length = findTextField.length

            if(idx != -1){
                noteTextArea.selectRange(idx, idx + length)
            }

        }
    }

    fun prevWord(){
        if(findTextField.text != null && findTextField.text != "") {

            if(idx == noteTextArea.text.indexOf(findTextField.text, 0, true)){
                idx = noteTextArea.text.lastIndexOf(findTextField.text, noteTextArea.text.length ,true)
            } else {
                idx = noteTextArea.text.lastIndexOf(findTextField.text, idx - 1, true)
            }

            var length = findTextField.length

            if (idx != -1) {
                noteTextArea.selectRange(idx, idx + length)
            }
        }
    }


    init {
        noteTextArea.addEventHandler(KeyEvent.KEY_RELEASED, EventHandler<KeyEvent>() {
            if (boldShortcutWindows.match(it) || boldShortcutMac.match(it)) {
                textEditingToolbar.boldButton.fire()
            } else if (underlineShortcutWindows.match(it) || underlineShortcutMac.match(it)) {
                textEditingToolbar.underlineButton.fire()
            } else if (italicsShortcutWindows.match(it) || italicsShortcutMac.match(it)) {
                textEditingToolbar.italicsButton.fire()
            }
        })

        noteTextArea.textProperty().addListener { observable, oldValue, newValue ->
            if (!model.changedNote) {
                var textStyles = mutableListOf<String>()
                if (newValue.length == oldValue.length+1) {
                    var charactersAdded = newValue.length - oldValue.length
                    var styleString = ""
                    if (textEditingToolbar.boldButton.isSelected) {
                        styleString += "-fx-font-weight: bold;"
                    }
                    if (textEditingToolbar.underlineButton.isSelected) {
                        styleString += "-fx-underline: true;"
                    }
                    if (textEditingToolbar.italicsButton.isSelected) {
                        styleString += "-fx-font-style: italic;"
                    }

                    textStyles = model.addStylesAtRange(caretIndex, charactersAdded, styleString)
                    model.updateCurrentText(newValue, textStyles)
                } else {
                    for (i in newValue.indices) {
                        textStyles.add(noteTextArea.getStyleOfChar(i))
                    }
                    model.updateCurrentText(newValue, textStyles)
                }
                println("model updating text through text listener: " + newValue)
            }
        }

        noteTextArea.selectionProperty().addListener { observable, old, new ->
            var selectionRange = getSelectionRange(new.toString())
            caretIndex = selectionRange[0]
            var boldToggle: Boolean
            var underlineToggle: Boolean
            var italicToggle: Boolean


            if (selectionRange[0] == selectionRange[1] && selectionRange[0] != 0) {
                selectionRange[0] -= 1
            }
            if (selectionRange[1] == 0) {
                boldToggle = false
                underlineToggle = false
                italicToggle = false
            } else {
                boldToggle = model.textRangeHasStyle("-fx-font-weight: bold;", selectionRange[0], selectionRange[1])
                underlineToggle = model.textRangeHasStyle("-fx-underline: true;", selectionRange[0], selectionRange[1])
                italicToggle = model.textRangeHasStyle("-fx-font-style: italic;", selectionRange[0], selectionRange[1])
            }
            textEditingToolbar.boldButton.isSelected = boldToggle
            model.setBoldToggleButton(boldToggle)
            textEditingToolbar.underlineButton.isSelected = underlineToggle
            model.setUnderlineToggleButton(underlineToggle)
            textEditingToolbar.italicsButton.isSelected = italicToggle
            model.setItalicToggleButton(italicToggle)
        }

        noteTextArea.prefHeightProperty().bind(this.heightProperty())
        noteTextArea.maxWidthProperty().bind(this.widthProperty())
        noteTextArea.isWrapText = true
        textEditingToolbar.boldButton.onAction = EventHandler {
            var selectionRange = getSelectionRange(noteTextArea.selection.toString())
            if (selectionRange[1] - selectionRange[0] != 0) {
                model.boldTextRange(selectionRange[0], selectionRange[1])
            }
            noteTextArea.requestFocus()
        }
        textEditingToolbar.underlineButton.onAction = EventHandler {
            var selectionRange = getSelectionRange(noteTextArea.selection.toString())
            if (selectionRange[1] - selectionRange[0] != 0) {
                model.underlineTextRange(selectionRange[0], selectionRange[1])
            }
            noteTextArea.requestFocus()
        }
        textEditingToolbar.italicsButton.onAction = EventHandler {
            var selectionRange = getSelectionRange(noteTextArea.selection.toString())
            if (selectionRange[1] - selectionRange[0] != 0) {
                model.italicTextRange(selectionRange[0], selectionRange[1])
            }
            noteTextArea.requestFocus()
        }

        textEditingToolbar.PDFButton.onMouseClicked = EventHandler {

            val fileChooser = FileChooser()
            //Set extension filter
            val extFilter = FileChooser.ExtensionFilter("Portable Document Format (PDF)", "*.pdf")
            fileChooser.extensionFilters.addAll(extFilter)

            //Show save file dialog
            val file = fileChooser.showSaveDialog(this.scene.window)
            //fileChooser.initialFileName

            if (file != null) {
                model.saveAsPDF(noteTextArea, file)
            }
        }

        dateModified.maxWidthProperty().bind(this.widthProperty())
        dateModified.font = Font.font("Helvetica", 12.0)
        dateModified.textFill = Color.GRAY

        val hbox = HBox()
        hbox.background = Background(BackgroundFill(Color.WHITE, null, null))
        hbox.children.add(dateModified)
        hbox.alignment = Pos.CENTER_RIGHT

        // searchBar
        searchBar.alignment = Pos.CENTER_RIGHT
        searchBar.background = Background(BackgroundFill(Color.WHITE, null, null))
        findTextField.promptText = "Find"
        findTextField.prefWidth = 200.0
        findTextField.prefHeight = 10.0


        findTextField.textProperty().addListener { observable, oldValue, newValue ->
            searchWord()
        }

        val prevButton = Button()
        val img = Image("prev.png")
        val prevView = ImageView(img)
        prevButton.graphic = prevView
        prevView.fitHeight = 10.0
        prevView.fitWidth = 10.0
        val nextButton = Button()
        val img2 = Image("next.png")
        val nextView = ImageView(img2)
        nextButton.graphic = nextView
        nextView.fitHeight = 10.0
        nextView.fitWidth = 10.0

        val doneButton = Button("Done")
        doneButton.setOnAction {
            children.remove(searchBar)
            findTextField.text = ""
            countLabel.text = ""
            idx = 0
        }

        nextButton.setOnAction { nextWord() }
        prevButton.setOnAction { prevWord() }

        searchBar.children.addAll(findTextField,countLabel ,prevButton, nextButton, doneButton)

        children.addAll(textEditingToolbar, hbox, noteTextArea)
        model.addView(this)
    }
}
