import javafx.embed.swing.JFXPanel
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

class MainTest {
    private val jfxPanel = JFXPanel()
    private val controller = Main()
    private val model = Model()

    @Test
    @DisplayName("Testing save folder")
    fun saveFolder() {
        val folder = Folder("Test", model)
        val expectedNumFolders = 1
        val expectedFolderCount: Long = 1
        controller.saveFolder(folder)
        assertEquals(expectedNumFolders, controller.model.folders.size)
        assertEquals(expectedFolderCount, controller.model.folderCount)
    }
}
