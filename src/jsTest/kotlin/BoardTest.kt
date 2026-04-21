import game.Board
import game.Player
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BoardTest {

    @Test
    fun new_board_is_empty_and_correctly_sized() {
        val board = Board(rows = 6, columns = 7)
        assertEquals(6, board.grid.size)
        assertEquals(7, board.grid[0].size)
        assertTrue(board.grid.flatten().all { it == null })
        assertFalse(board.isFull())
    }

    @Test
    fun drop_lands_in_bottom_row_first() {
        val board = Board(rows = 6, columns = 7)
        val result = board.drop(column = 3, player = Player.RED)
        assertNotNull(result)
        assertEquals(5, result.row)
        assertEquals(3, result.column)
        assertEquals(Player.RED, result.board[5, 3])
        assertNull(result.board[4, 3])
    }

    @Test
    fun pieces_stack_with_gravity() {
        var board = Board(4, 4)
        repeat(4) { i ->
            val player = if (i % 2 == 0) Player.RED else Player.YELLOW
            board = board.drop(0, player)!!.board
        }
        // bottom-up: RED, YELLOW, RED, YELLOW
        assertEquals(Player.RED, board[3, 0])
        assertEquals(Player.YELLOW, board[2, 0])
        assertEquals(Player.RED, board[1, 0])
        assertEquals(Player.YELLOW, board[0, 0])
    }

    @Test
    fun dropping_into_full_column_returns_null() {
        var board = Board(3, 3)
        repeat(3) { board = board.drop(1, Player.RED)!!.board }
        assertTrue(board.isColumnFull(1))
        assertNull(board.drop(1, Player.YELLOW))
    }

    @Test
    fun dropping_out_of_bounds_returns_null() {
        val board = Board(3, 3)
        assertNull(board.drop(-1, Player.RED))
        assertNull(board.drop(3, Player.RED))
    }

    @Test
    fun board_is_full_only_when_top_row_filled() {
        var board = Board(2, 2)
        listOf(0, 1, 0, 1).forEach { board = board.drop(it, Player.RED)!!.board }
        assertTrue(board.isFull())
    }

    @Test
    fun original_board_is_not_mutated() {
        val board = Board(3, 3)
        val after = board.drop(0, Player.RED)!!.board
        assertNull(board[2, 0])
        assertEquals(Player.RED, after[2, 0])
    }
}
