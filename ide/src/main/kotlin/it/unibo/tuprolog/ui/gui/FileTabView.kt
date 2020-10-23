package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.operators.OperatorSet
import javafx.event.Event
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Tab
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.io.File
import java.io.IOException

@Suppress("UNUSED_PARAMETER")
class FileTabView(
    private val file: File,
    private val model: PrologIDEModel,
    private val ideController: PrologIDEController,
    initialText: String = ""
) : Tab() {

    companion object {
        private const val FXML = "FileTabView.fxml"
    }

    private val syntaxColoring: SyntaxColoring

    init {
        val loader = FXMLLoader(FileTabView::class.java.getResource(FXML))
        loader.setController(this)
        loader.setRoot(this)

        try {
            loader.load<BorderPane>()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }

        codeArea.appendText(initialText)

        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        syntaxColoring = SyntaxColoring(codeArea)
        syntaxColoring.activate()

        text = file.name
    }

    @FXML
    lateinit var codeArea: CodeArea

    fun notifyOperators(operators: OperatorSet) {
        syntaxColoring.operators = operators
    }

//    @FXML
//    lateinit var btnClose: Button

    @FXML
    fun onTabSelectionChanged(e: Event) {
        if (isSelected) {
            model.selectFile(file)
        }
    }

    @FXML
    fun onMousePressedOnCodeArea(e: MouseEvent) {
        ideController.onMouseClickedOnCurrentFile(e)
    }

    @FXML
    fun onKeyTypedOnCodeArea(e: KeyEvent) {
        model.setFile(file, codeArea.text)
        ideController.onKeyTypedOnCurrentFile(e)
    }

    @FXML
    fun onKeyPressedOnCodeArea(e: KeyEvent) {
        model.setFile(file, codeArea.text)
        ideController.onKeyPressedOnCurrentFile(e)
    }
}
