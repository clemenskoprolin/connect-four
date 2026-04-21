package ui

import androidx.compose.runtime.Composable
import game.GameConfig
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLSelectElement

@Composable
fun ControlsView(
    config: GameConfig,
    onConfigChange: (GameConfig) -> Unit,
    onNewGame: () -> Unit,
    onResetAll: () -> Unit
) {
    Div(attrs = { classes("controls") }) {
        SizeSelect(
            label = "Rows",
            current = config.rows,
            range = GameConfig.MIN_SIZE..GameConfig.MAX_SIZE
        ) { v ->
            val maxWin = minOf(GameConfig.MAX_WIN, maxOf(v, config.columns))
            onConfigChange(config.copy(rows = v, winLength = config.winLength.coerceAtMost(maxWin)))
        }
        SizeSelect(
            label = "Columns",
            current = config.columns,
            range = GameConfig.MIN_SIZE..GameConfig.MAX_SIZE
        ) { v ->
            val maxWin = minOf(GameConfig.MAX_WIN, maxOf(config.rows, v))
            onConfigChange(config.copy(columns = v, winLength = config.winLength.coerceAtMost(maxWin)))
        }
        SizeSelect(
            label = "Win Length",
            current = config.winLength,
            range = GameConfig.MIN_WIN..minOf(GameConfig.MAX_WIN, maxOf(config.rows, config.columns))
        ) { v -> onConfigChange(config.copy(winLength = v)) }

        Div(attrs = { classes("control") }) {
            Span(attrs = { classes("ctrl-label") }) { Text("Actions") }
            Div(attrs = { classes("button-row") }) {
                Button(attrs = {
                    classes("btn", "btn-primary")
                    onClick { onNewGame() }
                }) { Text("New Game") }
                Button(attrs = {
                    classes("btn", "btn-secondary")
                    onClick { onResetAll() }
                }) { Text("Reset Scores") }
            }
        }
    }
}

@Composable
private fun SizeSelect(
    label: String,
    current: Int,
    range: IntRange,
    onSelect: (Int) -> Unit
) {
    Div(attrs = { classes("control") }) {
        Span(attrs = { classes("ctrl-label") }) { Text(label) }
        Div(attrs = { classes("select-wrap") }) {
            Select(attrs = {
                onChange { ev ->
                    (ev.target as? HTMLSelectElement)?.value?.toIntOrNull()?.let(onSelect)
                }
            }) {
                for (v in range) {
                    Option(value = v.toString(), attrs = {
                        if (v == current) attr("selected", "selected")
                    }) { Text(v.toString()) }
                }
            }
        }
    }
}
