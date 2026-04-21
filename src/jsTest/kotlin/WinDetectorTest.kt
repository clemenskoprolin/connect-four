import game.Board
import game.Cell
import game.Player
import game.WinDetector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class WinDetectorTest {

    /**
     * Builds a board from a visual layout where each string is a row.
     * 'R' = red, 'Y' = yellow, '.' = empty.
     */
    private fun board(vararg rowsLayout: String): Board {
        val grid = rowsLayout.map { row ->
            row.map { ch ->
                when (ch) {
                    'R' -> Player.RED
                    'Y' -> Player.YELLOW
                    '.' -> null
                    else -> error("Invalid char: $ch")
                }
            }
        }
        return Board(rowsLayout.size, rowsLayout.first().length, grid)
    }

    @Test
    fun detects_horizontal_win() {
        val b = board(
            "....",
            "....",
            "....",
            "RRRR"
        )
        val win = WinDetector.findWin(b, row = 3, column = 3, winLength = 4)
        assertNotNull(win)
        assertEquals(4, win.size)
    }

    @Test
    fun detects_vertical_win() {
        val b = board(
            "....",
            "Y...",
            "Y...",
            "Y...",
            "Y..."
        )
        val win = WinDetector.findWin(b, row = 1, column = 0, winLength = 4)
        assertNotNull(win)
        assertEquals(listOf(Cell(1, 0), Cell(2, 0), Cell(3, 0), Cell(4, 0)), win)
    }

    @Test
    fun detects_diagonal_down_right() {
        val b = board(
            "R...",
            ".R..",
            "..R.",
            "...R"
        )
        val win = WinDetector.findWin(b, row = 2, column = 2, winLength = 4)
        assertNotNull(win)
        assertEquals(4, win.size)
    }

    @Test
    fun detects_diagonal_down_left() {
        val b = board(
            "...R",
            "..R.",
            ".R..",
            "R..."
        )
        val win = WinDetector.findWin(b, row = 1, column = 2, winLength = 4)
        assertNotNull(win)
        assertEquals(4, win.size)
    }

    @Test
    fun no_win_when_run_too_short() {
        val b = board(
            "....",
            "....",
            "....",
            "RRR."
        )
        assertNull(WinDetector.findWin(b, row = 3, column = 2, winLength = 4))
    }

    @Test
    fun no_win_when_run_broken_by_opponent() {
        val b = board(
            "....",
            "....",
            "....",
            "RRYR"
        )
        assertNull(WinDetector.findWin(b, row = 3, column = 3, winLength = 4))
    }

    @Test
    fun longer_runs_keep_the_last_move_inside_the_highlighted_segment() {
        val b = board("RRRRR")
        val win = WinDetector.findWin(b, row = 0, column = 4, winLength = 4)
        assertNotNull(win)
        assertEquals(listOf(Cell(0, 1), Cell(0, 2), Cell(0, 3), Cell(0, 4)), win)
    }

    @Test
    fun supports_configurable_win_length() {
        val b = board(
            "RRRRR",
            ".....",
            ".....",
            ".....",
            "....."
        )
        assertNotNull(WinDetector.findWin(b, row = 0, column = 0, winLength = 5))
        assertNull(WinDetector.findWin(b, row = 0, column = 0, winLength = 6))
    }
}
