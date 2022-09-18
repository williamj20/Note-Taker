import javafx.embed.swing.JFXPanel
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

class ModelTest {
    private val jfxPanel = JFXPanel()
    private val model = Model()
    private val controller = Main()

    @Test
    @DisplayName("Testing filtering the folders to be displayed")
    fun filterFolders() {
        val testFolder = Folder("TestOne", model)
        val testFolderTwo = Folder("Test2", model)
        model.folders.add(testFolder)
        model.folders.add(testFolderTwo)

        model.filterFoldersToDisplay("")
        assertEquals(2, model.foldersToDisplay.size)

        model.filterFoldersToDisplay("tEst")
        assertEquals(2, model.foldersToDisplay.size)

        model.filterFoldersToDisplay("one")
        assertEquals("TestOne", model.foldersToDisplay[model.foldersToDisplay.size-1].folderName)

        model.filterFoldersToDisplay("2")
        assertEquals("Test2", model.foldersToDisplay[model.foldersToDisplay.size-1].folderName)

        model.filterFoldersToDisplay("doesntExist")
        assertEquals(0, model.foldersToDisplay.size)
    }

    @Test
    @DisplayName("Testing setting selected note")
    fun setAsSelectedNote() {
        val testNote = Note("title", 77, null, controller, model)
        model.setAsSelectedNote(testNote)
        assertEquals(77, model.currentNoteId)
        assertEquals(testNote, model.currentNote)

        val newTestNote = Note("newTitle", 544, null, controller, model)
        model.setAsSelectedNote(newTestNote)
        assertEquals(544, model.currentNoteId)
        assertEquals(newTestNote, model.currentNote)
    }

    @Test
    @DisplayName("Testing saving note to a folder")
    fun saveNote() {
        val testFolder = Folder("ParentFolder", model)
        val testNote = Note("title", 4, testFolder, controller, model)
        model.saveNote(testNote)

        assertEquals(1, testFolder.noteslist.size)
        assertEquals(testNote, testFolder.noteslist[0])

        val newTestNote = Note("newTitle", 7, testFolder, controller, model)
        model.saveNote(newTestNote)
        assertEquals(2, testFolder.noteslist.size)
        assertEquals(newTestNote, testFolder.noteslist[1])
    }

    @Test
    @DisplayName("Testing deleting note from a folder")
    fun deleteNote() {
        val testFolder = Folder("ParentFolder", model)
        val testNote = Note("title", 4, testFolder, controller, model)
        model.saveNote(testNote)
        val newTestNote = Note("newTitle", 7, testFolder, controller, model)
        model.saveNote(newTestNote)

        model.deleteNote(testNote)
        assertEquals(1, testFolder.noteslist.size)
        assertEquals(newTestNote, testFolder.noteslist[0])

        model.deleteNote(newTestNote)
        assertEquals(0, testFolder.noteslist.size)
    }

    @Test
    @DisplayName("Testing renaming note")
    fun renameNote() {
        val testNote = Note("title", 4, null, controller, model)
        val newName = "this is new name of testNote"
        model.renameNote(testNote, newName)
        assertEquals(newName, testNote.title)
    }

    @Test
    @DisplayName("Testing updating body of note")
    fun updateCurrentText() {
        val testNote = Note("title", 5, null, controller, model)
        val initBody = "this is a body of the note"
        val styleList: MutableList<String> = mutableListOf()
        testNote.body = initBody
        val newBody = "NEW BODY TEXT"
        model.setAsSelectedNote(testNote)
        model.updateCurrentText(newBody, styleList)
        assertEquals(newBody, model.currentNote?.body)
    }
}
