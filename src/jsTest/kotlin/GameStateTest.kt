import game.GameConfig
import game.GameState
import game.GameStatus
import game.Player
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameStateTest {

    private fun classic() = GameState.initial(GameConfig.Classic)

    @Test
    fun initial_state_starts_with_red() {
        val s = classic()
        assertEquals(Player.RED, s.currentPlayer)
        assertEquals(GameStatus.PLAYING, s.status)
        assertEquals(0, s.moveCount)
        assertNull(s.winner)
    }

    @Test
    fun playing_alternates_players() {
        val s = classic().play(0).play(1).play(0)
        // RED, YELLOW, RED → next up YELLOW
        assertEquals(Player.YELLOW, s.currentPlayer)
        assertEquals(3, s.moveCount)
    }

    @Test
    fun illegal_move_returns_same_state() {
        var s = classic()
        // fill column 0
        repeat(s.config.rows) { s = s.play(0) }
        val before = s
        val after = s.play(0)
        assertEquals(before, after)
    }

    @Test
    fun horizontal_win_ends_the_game_and_increments_score() {
        // RED plays cols 0..3, YELLOW plays cols 4..6 in between
        var s = classic()
        s = s.play(0) // R
        s = s.play(4) // Y
        s = s.play(1) // R
        s = s.play(5) // Y
        s = s.play(2) // R
        s = s.play(6) // Y
        s = s.play(3) // R wins
        assertEquals(GameStatus.WIN, s.status)
        assertEquals(Player.RED, s.winner)
        assertEquals(4, s.winningCells.size)
        assertEquals(1, s.score.red)
        assertEquals(0, s.score.yellow)
    }

    @Test
    fun moves_are_blocked_after_a_win() {
        var s = classic()
        listOf(0, 4, 1, 5, 2, 6, 3).forEach { s = s.play(it) }
        val frozen = s.play(0)
        assertEquals(s, frozen)
    }

    @Test
    fun draw_when_board_fills_without_a_winner() {
        // Construct a scenario in a tiny 4x4 with winLength = 5 so no win is possible.
        var s = GameState.initial(GameConfig(rows = 4, columns = 4, winLength = 5))
        for (col in 0 until 4) {
            repeat(4) { s = s.play(col) }
        }
        assertEquals(GameStatus.DRAW, s.status)
        assertNull(s.winner)
        assertTrue(s.board.isFull())
    }

    @Test
    fun new_round_alternates_starter_and_keeps_score() {
        var s = classic()
        // RED wins quickly
        listOf(0, 4, 1, 5, 2, 6, 3).forEach { s = s.play(it) }
        val nextRound = s.newRound()
        assertEquals(Player.YELLOW, nextRound.currentPlayer)
        assertEquals(0, nextRound.moveCount)
        assertEquals(1, nextRound.score.red)
    }

    @Test
    fun new_round_with_new_config_alternates_starter_and_applies_config() {
        var s = classic()
        listOf(0, 4, 1, 5, 2, 6, 3).forEach { s = s.play(it) }

        val newConfig = GameConfig(rows = 8, columns = 9, winLength = 5)
        val nextRound = s.newRound(newConfig)

        assertEquals(newConfig, nextRound.config)
        assertEquals(Player.YELLOW, nextRound.currentPlayer)
        assertEquals(Player.YELLOW, nextRound.starter)
        assertEquals(0, nextRound.moveCount)
        assertEquals(1, nextRound.score.red)
    }

    @Test
    fun last_move_is_tracked_for_animation() {
        val s = classic().play(3)
        assertNotNull(s.lastMove)
        assertEquals(s.config.rows - 1, s.lastMove!!.row)
        assertEquals(3, s.lastMove!!.column)
    }
}
