package ui

import androidx.compose.runtime.Composable
import game.GameState
import game.GameStatus
import game.Player
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun StatusBar(state: GameState) {
    Div(attrs = { classes("status-row") }) {
        StatusPill(state)
        Div(attrs = { classes("score-group") }) {
            ScoreChip(Player.RED, state.score.red)
            ScoreChip(Player.YELLOW, state.score.yellow)
        }
    }
}

@Composable
private fun StatusPill(state: GameState) {
    val (player, message) = when (state.status) {
        GameStatus.PLAYING -> state.currentPlayer to "${state.currentPlayer.label}'s turn"
        GameStatus.WIN -> state.winner!! to "${state.winner!!.label} wins"
        GameStatus.DRAW -> state.currentPlayer to "Draw"
    }
    Div(attrs = { classes("status-pill") }) {
        Span(attrs = { classes("dot", player.cssClass) }) {}
        Text(message)
    }
}

@Composable
private fun ScoreChip(player: Player, value: Int) {
    Div(attrs = { classes("score") }) {
        Span(attrs = { classes("dot", player.cssClass) }) {}
        Text("${player.label} · $value")
    }
}
