package game

object WinDetector {
    private val DIRECTIONS = listOf(
        0 to 1,   // horizontal —
        1 to 0,   // vertical |
        1 to 1,   // diagonal \
        1 to -1   // diagonal /
    )

    /**
     * Looks for a winning run of [winLength] passing through ([row], [column]).
     * Returns the winning cells (ordered along the line) or null if there is no win.
     */
    fun findWin(board: Board, row: Int, column: Int, winLength: Int): List<Cell>? {
        val player = board[row, column] ?: return null
        for ((dr, dc) in DIRECTIONS) {
            val line = collectLine(board, row, column, dr, dc, player)
            if (line.size >= winLength) return winningWindow(line, row, column, winLength)
        }
        return null
    }

    private fun winningWindow(
        line: List<Cell>,
        row: Int,
        column: Int,
        winLength: Int
    ): List<Cell> {
        val anchorIndex = line.indexOfFirst { it.row == row && it.column == column }
        check(anchorIndex >= 0) { "winning line must contain the last move" }
        // Raw board states can contain longer runs, so keep a contiguous winning
        // segment that still includes the move we just evaluated.
        val start = (anchorIndex - winLength + 1).coerceIn(0, line.size - winLength)
        return line.subList(start, start + winLength)
    }

    private fun collectLine(
        board: Board,
        row: Int,
        column: Int,
        dr: Int,
        dc: Int,
        player: Player
    ): List<Cell> {
        val cells = ArrayDeque<Cell>()
        cells.add(Cell(row, column))
        // walk forward
        var r = row + dr; var c = column + dc
        while (board.isInside(r, c) && board[r, c] == player) {
            cells.addLast(Cell(r, c)); r += dr; c += dc
        }
        // walk backward
        r = row - dr; c = column - dc
        while (board.isInside(r, c) && board[r, c] == player) {
            cells.addFirst(Cell(r, c)); r -= dr; c -= dc
        }
        return cells.toList()
    }
}
