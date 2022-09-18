import javafx.embed.swing.JFXPanel
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Conversions.toLong

internal class FolderViewTest {

    private val jfxPanel = JFXPanel()
    val model = Model()
    val controller = Main()
    val testFolder: Folder = Folder("Test", model)
    val testNote = Note("Test Note", 0, null, controller, model)
    val testFolderView: FolderView = FolderView(model, controller)

    @Test
    @DisplayName("Create folder test")
    fun newFolder() {
        val expected = 1;
        testFolderView.newFolder(testFolder)
        assertEquals(expected, model.folders.size)
        assertEquals(expected, testFolderView.folders.panes.size)
    }

    @Test
    @DisplayName("Add note to Folder's pane")
    fun addNotes() {
        testFolderView.addNotes(testFolder, testNote)
        val expected = testFolder.notes
        assertEquals(expected, testFolder.folderPane.content)
    }

    @Test
    @DisplayName("Delete Folder Test")
    fun deleteFolder(){
        testFolderView.newFolder(testFolder)
        testFolderView.deleteFolder(testFolder)
        val expected = 0
        assertEquals(expected, model.folders.size)
        assertEquals(expected.toLong(), model.folderCount)
        assertEquals(expected, testFolderView.folders.panes.size)
    }
}