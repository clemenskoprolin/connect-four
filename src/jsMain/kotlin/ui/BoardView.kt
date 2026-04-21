package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import game.GameState
import org.jetbrains.compose.web.dom.Div

@Composable
fun BoardView(state: GameState, onColumnClick: (Int) -> Unit) {
    var hoveredColumn by remember { mutableStateOf<Int?>(null) }

    Div(attrs = {
        classes("board-container")
        style {
            property("--cols", state.config.columns.toString())
            property("--rows", state.config.rows.toString())
        }
    }) {
        IndicatorRow(state, hoveredColumn)
        Board(
            state = state,
            onHover = { hoveredColumn = it },
            onColumnClick = onColumnClick
        )
    }
}

@Composable
private fun IndicatorRow(state: GameState, hoveredColumn: Int?) {
    Div(attrs = { classes("indicator-row") }) {
        for (c in 0 until state.config.columns) {
            val visible = !state.isOver &&
                hoveredColumn == c &&
                !state.board.isColumnFull(c)
            Div(attrs = { classes("indicator-cell") }) {
                Div(attrs = {
                    val css = buildList {
                        add("indicator-piece")
                        add(state.currentPlayer.cssClass)
                        if (visible) add("visible")
                    }
                    classes(*css.toTypedArray())
                })
            }
        }
    }
}

@Composable
private fun Board(
    state: GameState,
    onHover: (Int?) -> Unit,
    onColumnClick: (Int) -> Unit
) {
    Div(attrs = {
        classes("board")
        onMouseLeave { onHover(null) }
    }) {
        for (c in 0 until state.config.columns) {
            Column(state, c, onHover, onColumnClick)
        }
    }
}

@Composable
private fun Column(
    state: GameState,
    column: Int,
    onHover: (Int?) -> Unit,
    onColumnClick: (Int) -> Unit
) {
    val full = state.board.isColumnFull(column)
    val disabled = full || state.isOver
    Div(attrs = {
        val css = if (disabled) arrayOf("col", "disabled") else arrayOf("col")
        classes(*css)
        onMouseEnter { onHover(column) }
        onClick { if (!disabled) onColumnClick(column) }
    }) {
        for (r in 0 until state.config.rows) {
            Cell(state, r, column)
        }
    }
}

@Composable
private fun Cell(state: GameState, row: Int, column: Int) {
    Div(attrs = { classes("cell") }) {
        val player = state.board[row, column] ?: return@Div
        val isLast = state.lastMove?.row == row && state.lastMove?.column == column
        val isWinner = state.winningCells.any { it.row == row && it.column == column }
        Div(attrs = {
            val css = buildList {
                add("piece")
                add(player.cssClass)
                if (isLast) add("animated")
                if (isWinner) add("winner")
            }
            classes(*css.toTypedArray())
            if (isLast) {
                style { property("--drop-height", "${(row + 1) * 100}%") }
            }
        })
    }
}
