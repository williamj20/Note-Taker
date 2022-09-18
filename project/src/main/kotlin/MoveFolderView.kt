import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage

class MoveFolderView(model: Model, note:Note) : IView {

    var folders = model.folders
    var borderPane = BorderPane()
    var vBox = VBox()
    var moveButton = Button("Move")
    var selectedFolder: Folder = folders[0]
    var originalFolder = note.parentFolder?.folderName

    override fun updateView() {
        vBox.children.removeAll(vBox.children)
        for(folder in folders){
            var name = folder.folderName
            var hBox = HBox()
            hBox.alignment = Pos.CENTER
            var button = Button(name)
            button.prefWidth = 220.0
            if(name == originalFolder) button.isDisable = true
            button.setOnAction {
                moveButton.isDisable = false
                selectedFolder = folder
            }
            hBox.children.add(button)
            vBox.children.add(hBox)
        }
    }

    init{

        // for move button
        var hBox = HBox()
        HBox.setMargin(moveButton, Insets(0.0, 0.0, 5.0, 0.0))
        hBox.alignment = Pos.CENTER
        moveButton.isDisable = true
        hBox.children.add(moveButton)
        borderPane.bottom = hBox
        borderPane.center = vBox

        var scrollPane = ScrollPane()

        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        scrollPane.content = borderPane
        scrollPane.isFitToWidth = true
        scrollPane.isFitToHeight = true

        val scene = Scene(scrollPane, 220.0, 250.0)
        val stage = Stage()
        stage.title = "Select a Location"
        stage.scene = scene
        stage.show()
        stage.isResizable = false
        model.addView(this)

        moveButton.setOnAction {
            model.moveNote(note, selectedFolder)
            stage.close()
        }
    }
}