package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import game.GameConfig
import game.GameState
import game.GameStatus
import game.Player
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import persistence.GameStorage
import persistence.ThemeStorage

@Composable
fun App() {
    var state by remember {
        mutableStateOf(GameStorage.load() ?: GameState.initial(GameConfig.Classic))
    }
    var theme by remember { mutableStateOf(ThemeStorage.load() ?: Theme.Dark) }

    DisposableEffect(theme) {
        applyTheme(theme)
        ThemeStorage.save(theme)
        onDispose { }
    }

    fun update(next: GameState) {
        state = next
        GameStorage.save(next)
    }

    Div(attrs = { classes("app") }) {
        Header(theme = theme, onToggleTheme = { theme = theme.opposite() })

        Div(attrs = { classes("card") }) {
            StatusBar(state)
        }

        BoardView(
            state = state,
            onColumnClick = { column -> update(state.play(column)) }
        )

        if (state.isOver) {
            ResultToast(state)
        }

        Div(attrs = { classes("card") }) {
            ControlsView(
                config = state.config,
                onConfigChange = { newCfg -> update(state.newRound(newCfg)) },
                onNewGame = { update(state.newRound()) },
                onResetAll = {
                    GameStorage.clear()
                    update(GameState.initial(state.config, Player.RED))
                }
            )
        }

        Div(attrs = { classes("footer") }) {
            Text("Built with Compose HTML · Kotlin/JS · State persists across refreshes")
        }
    }
}

@Composable
private fun Header(theme: Theme, onToggleTheme: () -> Unit) {
    val title = if (theme == Theme.Dark) "Switch to light mode" else "Switch to dark mode"
    Div(attrs = { classes("header") }) {
        Div(attrs = { classes("header-spacer") }) {}
        Div(attrs = { classes("header-titles") }) {
            H1(attrs = { classes("title") }) { Text("Connect Four") }
            P(attrs = { classes("subtitle") }) {
                Text("Two players · drop · stack · align — configurable size and win length")
            }
        }
        Button(attrs = {
            classes("theme-toggle")
            onClick { onToggleTheme() }
            attr("aria-label", title)
            attr("aria-pressed", (theme == Theme.Light).toString())
            attr("title", title)
        }) {
            Span(attrs = { classes("theme-toggle-track") }) {
                Span(attrs = { classes("theme-toggle-thumb") }) {}
            }
            Div(attrs = { classes("theme-toggle-copy") }) {
                Span(attrs = { classes("theme-toggle-label") }) { Text("Theme") }
                Span(attrs = { classes("theme-toggle-value") }) {
                    Text(if (theme == Theme.Dark) "Dark" else "Light")
                }
            }
        }
    }
}

@Composable
private fun ResultToast(state: GameState) {
    val (variant, message) = when (state.status) {
        GameStatus.WIN -> {
            val w = state.winner!!
            val v = if (w == Player.RED) "win-red" else "win-yellow"
            v to "${w.label} connected ${state.config.winLength}!"
        }
        GameStatus.DRAW -> "draw" to "Draw — the board is full."
        GameStatus.PLAYING -> "" to ""
    }
    Div(attrs = { classes("toast", variant) }) { Text(message) }
}
