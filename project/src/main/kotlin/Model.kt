import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.fxmisc.richtext.InlineCssTextArea
import java.awt.SystemColor
import java.awt.SystemColor.text
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class Model {
    // in this file, we have the model
    // create variable for the data
    // create functions to set/get/modify this data

    // all views of this model
    private val views: ArrayList<IView> = ArrayList()
    val folders: MutableList<Folder> = mutableListOf()
    var foldersToDisplay: List<Folder> = listOf()
    var noteCount = 0
    var folderCount: Long = 0
    var textStyles: MutableList<String> = mutableListOf()
    var currentText = ""
    var currentNoteId = 0
    var currentNote: Note? = null
    private var idTracker = 0
    var folderIdTracker = 0
    var boldToggle = false
    var italicToggle = false
    var underlineToggle = false
    var password: String = ""
    var changedNote = false
    var folderIndexToDelete: Int = 0


    fun addView(view: IView) {
        views.add(view)
        view.updateView()
    }

    private fun notifyObservers() {
        for (view in views) {
            view.updateView()
        }
    }

    fun setAsSelectedNote(note: Note) {
        currentNote = note
        currentNoteId = note.id
        println("note title: " + note.title + " and id: " + note.id)
        println("${note.title} has been selected")
        changedNote = true
        notifyObservers()
    }

    fun noteChanged() {
        changedNote = false
    }

    // call this function to update the last modified date of a note
    private fun adjustLastModifiedDate(note: Note) {
        note.dateModified = SimpleDateFormat("MMMM dd, yyyy hh:mm a").format(Calendar.getInstance().time)
    }

    // called when the note view textarea has changed
    fun updateCurrentText(text: String, styleList: MutableList<String>) {
        currentNote?.body = text
        //currentNote?.textStyles?.clear()
        currentNote?.textStyles = styleList
        println("current text updated")
        println(styleList)
        adjustLastModifiedDate(currentNote!!)
        notifyObservers()
    }

    fun updateTextStyles(styleList: MutableList<String>) {
        currentNote?.textStyles = styleList
    }

    fun getAndUpdateNewNoteId(): Int {
        return idTracker++
    }

    fun getAndUpdateNewFolderId(): Int {
        return folderIdTracker++
    }

    fun saveNote(note: Note) {
        note.parentFolder?.addNote(note)
        println("notecount is $noteCount")
        setAsSelectedNote(note)
        notifyObservers()
    }

    fun duplicateNote(note: Note, newNote: Note){
        note.parentFolder?.duplicationNote(note, newNote)
        notifyObservers()
    }

    // helper function that resets the selected note and all its properties to null
    private fun resetSelectedNote() {
        currentNote = null
        currentNoteId = -1
        changedNote = true
    }

    fun deleteNote(note: Note) {
        noteCount -= 1
        note.parentFolder?.deleteNote(note.id)
        if (note == currentNote) {
            resetSelectedNote()
        }
        notifyObservers()
    }

    fun pinNote(note: Note){
        note.parentFolder?.pinNote(note)
        notifyObservers()
    }

    fun deleteFolder(folder: Folder) {
        folderCount -= 1
        if (currentNote?.parentFolder == folder) {
            resetSelectedNote()
        }
        noteCount -= folder.noteCount
        folderIndexToDelete = folders.indexOf(folder)
        folders.remove(folder)
        notifyObservers()
    }

    fun moveNote(note: Note, destination: Folder){
        println("move")
        note.parentFolder?.deleteNote(note.id)
        destination.addNote(note)
        note.parentFolder = destination
        notifyObservers()
    }

    fun updateTextOnly(text: String) {
        currentNote?.body = text
    }

    fun addStylesAtRange(index: Int, charactersAdded: Int, style: String): MutableList<String> {
        if (index == currentNote?.textStyles!!.size) {
            for (i in 0 until charactersAdded) {
                currentNote?.textStyles!!.add(style)
            }
        } else {
            var j = 0
            var newStyles = mutableListOf<String>()
            while (j < currentNote?.textStyles!!.size) {
                if (j == index) {
                    for (i in 0 until charactersAdded) {
                        newStyles.add(style)
                    }
                }
                newStyles.add(currentNote?.textStyles!![j])
                j += 1
            }
            currentNote?.textStyles = newStyles.toList() as MutableList<String>
        }
        return currentNote?.textStyles!!
    }

    fun getStyleList(): MutableList<String>? {
        return currentNote?.textStyles
    }

    fun addTextStyle(style: String, start: Int, end: Int) {
        for (i in start until end) {
            if (style !in currentNote?.textStyles!![i]) {
                println("adding text style: " + style)
                currentNote?.textStyles!![i] += style
            }
        }
    }

    fun removeTextStyle(style1: String, style2: String, start: Int, end: Int) {
        for (i in start until end) {
            var newStyle = ""
            if (style1 in currentNote?.textStyles!![i]) {
                newStyle += style1
            }
            if (style2 in currentNote?.textStyles!![i]) {
                newStyle += style2
            }
            currentNote?.textStyles!![i] = newStyle
        }
    }

    fun textRangeHasStyle(style: String, start: Int, end: Int): Boolean {
        for (i in start until end) {
            if (style !in currentNote?.textStyles!![i]) {
                return false
            }
        }
        return true
    }

    fun setBoldToggleButton(boolean: Boolean) {
        boldToggle = boolean
    }

    fun setUnderlineToggleButton(boolean: Boolean) {
        underlineToggle = boolean
    }

    fun setItalicToggleButton(boolean: Boolean) {
        italicToggle = boolean
    }

    // called when the bold button is clicked
    // the behaviour is similar to that of google docs and other word applications
    // if there is an unbolded character in the range, then we bold then entire range
    // else, we unbold the entire range
    fun boldTextRange(start: Int, end: Int) {
        var allBolded = textRangeHasStyle("-fx-font-weight: bold;", start, end)
        boldToggle = if (!allBolded) {
            addTextStyle("-fx-font-weight: bold;", start, end)
            true
        } else {
            removeTextStyle("-fx-underline: true;", "-fx-font-style: italic;", start, end)
            false
        }
        notifyObservers()
    }

    // called when the underline button is clicked
    // if there is a NOT underlined character in the range, then we remove the underline from the entire range
    // else, we underline the entire range
    fun underlineTextRange(start: Int, end: Int) {
        var allUnderlined = textRangeHasStyle("-fx-underline: true;", start, end)
        underlineToggle = if (!allUnderlined) {
            addTextStyle("-fx-underline: true;", start, end)
            true
        } else {
            removeTextStyle("-fx-font-weight: bold;", "-fx-font-style: italic;", start, end)
            false
        }
        notifyObservers()
    }

    // called when the italic button is clicked
    // if there is a NOT italic character in the range, then we remove the italics from the entire range
    // else, we italicize the entire range
    fun italicTextRange(start: Int, end: Int) {
        var allItalicized = textRangeHasStyle("-fx-font-style: italic;", start, end)
        italicToggle = if (!allItalicized) {
            addTextStyle("-fx-font-style: italic;", start, end)
            true
        } else {
            removeTextStyle("-fx-font-weight: bold;", "-fx-underline: true;", start, end)
            false
        }
        notifyObservers()
    }

    fun filterFoldersToDisplay(filter: String) {
        foldersToDisplay = folders.filter { folder -> folder.folderName.lowercase().contains(filter.lowercase()) }
    }

    fun renameNote(note: Note, newName: String) {
        note.title = newName
        adjustLastModifiedDate(note)
        notifyObservers()
    }

    fun renameFolder(folder: Folder) {
        notifyObservers()
    }

    fun saveAsPDF(textArea: InlineCssTextArea, file : File){
        // solution to programming problem revolving around formatting pdf with PDFBox was partially used from:
        // https://stackoverflow.com/questions/19635275/how-to-generate-multiple-lines-in-pdf-using-apache-pdfbox
        // "it is common practice to use third-party libraries and sources found online to solve programming problems.
        // For this reason, the team is allowed to use third-party source or libraries for their project provided that
        // (a) they document the source of this contribution in source code, typically as a comment, and in their README file, and
        // (b) no single source constitutes more than 10% of their project"
        var doc = PDDocument()
        var page = PDPage()
        doc.addPage(page);
        var content = PDPageContentStream(doc, page)

        var pdfFont = PDType1Font.HELVETICA
        var fontSize = 11.5.toFloat()
        var leading = 1.5 * fontSize

        var mediabox = page.getMediaBox()
        var margin = 72.0.toFloat()
        val width: Float = mediabox.getWidth() - 2 * margin
        var startX: Float = mediabox.getLowerLeftX() + margin
        var startY: Float = mediabox.getUpperRightY() - margin
        var currentY = startY

        var began = false
        var textSplitByNewLine = textArea.text.lines()
        for (textLine in textSplitByNewLine) {
            var text = textLine
            val lines: MutableList<String> = mutableListOf<String>()
            var lastSpace = -1
            while (text.length > 0) {
                var spaceIndex: Int = text.indexOf(' ', lastSpace + 1)
                if (spaceIndex < 0) {
                    spaceIndex = text.length
                }
                var subString: String? = text.substring(0, spaceIndex)
                val size: Float = fontSize * pdfFont.getStringWidth(subString) / 1000
                if (size > width) {
                    if (lastSpace < 0) {
                        lastSpace = spaceIndex
                    }
                    subString = text.substring(0, lastSpace)
                    lines.add(subString)
                    text = text.substring(lastSpace).trim()
                    lastSpace = -1
                } else if (spaceIndex == text.length) {
                    lines.add(text)
                    text = ""
                } else {
                    lastSpace = spaceIndex
                }
            }

            if (!began) {
                content.beginText()
                content.setFont(pdfFont, fontSize)
                content.newLineAtOffset(startX, startY)
                began = true
            }

            for (line in lines) {
                currentY -= leading.toFloat()
                if (currentY <= margin) {
                    content.endText()
                    content.close()
                    val new_Page = PDPage()
                    doc.addPage(new_Page)
                    content = PDPageContentStream(doc, new_Page)
                    content.beginText()
                    content.setFont(pdfFont, fontSize)
                    content.newLineAtOffset(startX, startY)
                    currentY = startY
                }
                content.showText(line)
                content.newLineAtOffset(0.toFloat(), -leading.toFloat())
            }
            content.newLineAtOffset(0.toFloat(), (-15).toFloat())
            currentY -= leading.toFloat()
        }
        content.endText()
        content.close()
        doc.save(file.absolutePath)
        doc.close()
    }

}
