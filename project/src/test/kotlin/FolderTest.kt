import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javafx.embed.swing.JFXPanel
import org.junit.jupiter.api.DisplayName

internal class FolderTest{

    private val jfxPanel = JFXPanel()
    private val model = Model()
    private val controller = Main()
    val testFolder: Folder = Folder("Test", model)
    val testNote = Note("Test Note", 0, testFolder, controller,model)

    @Test
    @DisplayName("Add note to folder")
    fun addNote() {
        testFolder.addNote(testNote)
        val expected = 1
        assertEquals(expected, testFolder.noteCount, "Number of note is one")
    }

    @Test
    @DisplayName("Delete note from folder")
    fun deleteNote() {
        testFolder.addNote(testNote)
        testFolder.deleteNote(testNote.id)
        val expected = 0
        assertEquals(expected, testFolder.noteCount, "Number of note is zero")
    }

    @Test
    @DisplayName("Rename Folder")
    fun renameFolder(){
        val expected = "new"
        testFolder.renameFolder("new")
        assertEquals(expected, testFolder.folderName)
    }
}
