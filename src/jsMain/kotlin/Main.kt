import org.jetbrains.compose.web.renderComposable
import persistence.ThemeStorage
import ui.App
import ui.Theme
import ui.applyTheme

fun main() {
    applyTheme(ThemeStorage.load() ?: Theme.Dark)
    renderComposable(rootElementId = "root") {
        App()
    }
}
