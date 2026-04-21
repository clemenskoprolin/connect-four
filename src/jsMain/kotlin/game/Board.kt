package game

import kotlinx.serialization.Serializable

/**
 * Immutable board. Row 0 is the TOP row, the last row is the bottom.
 * `drop` mimics gravity: pieces settle on the lowest empty row of a column.
 */
@Serializable
data class Board(
    val rows: Int,
    val columns: Int,
    val grid: List<List<Player?>> = List(rows) { List(columns) { null } }
) {
    init {
        require(rows > 0 && columns > 0) { "rows and columns must be positive" }
        require(grid.size == rows) { "grid row count mismatch" }
        require(grid.all { it.size == columns }) { "grid column count mismatch" }
    }

    operator fun get(row: Int, column: Int): Player? = grid[row][column]

    fun isInside(row: Int, column: Int): Boolean =
        row in 0 until rows && column in 0 until columns

    fun isColumnFull(column: Int): Boolean = grid[0][column] != null

    fun isFull(): Boolean = grid[0].all { it != null }

    /** Returns the new board and the landing coordinates, or null when the column is full / invalid. */
    fun drop(column: Int, player: Player): DropResult? {
        if (column !in 0 until columns) return null
        val targetRow = (rows - 1 downTo 0).firstOrNull { grid[it][column] == null } ?: return null
        val newGrid = grid.mapIndexed { r, row ->
            if (r == targetRow) row.toMutableList().also { it[column] = player } else row
        }
        return DropResult(copy(grid = newGrid), targetRow, column)
    }
}

data class DropResult(val board: Board, val row: Int, val column: Int)
