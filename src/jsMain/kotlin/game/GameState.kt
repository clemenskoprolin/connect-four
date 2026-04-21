package game

import kotlinx.serialization.Serializable

@Serializable
enum class GameStatus { PLAYING, WIN, DRAW }

@Serializable
data class Score(val red: Int = 0, val yellow: Int = 0) {
    fun increment(player: Player): Score = when (player) {
        Player.RED -> copy(red = red + 1)
        Player.YELLOW -> copy(yellow = yellow + 1)
    }

    fun forPlayer(player: Player): Int = when (player) {
        Player.RED -> red
        Player.YELLOW -> yellow
    }
}

@Serializable
data class GameState(
    val config: GameConfig,
    val board: Board,
    val currentPlayer: Player,
    val status: GameStatus,
    val winner: Player? = null,
    val winningCells: List<Cell> = emptyList(),
    val lastMove: Cell? = null,
    val moveCount: Int = 0,
    val score: Score = Score(),
    val starter: Player = Player.RED
) {
    val isOver: Boolean get() = status != GameStatus.PLAYING

    /** Drops a piece in [column] for the current player. Returns the same state if the move is illegal. */
    fun play(column: Int): GameState {
        if (isOver) return this
        val drop = board.drop(column, currentPlayer) ?: return this
        val winLine = WinDetector.findWin(drop.board, drop.row, drop.column, config.winLength)
        val nextStatus = when {
            winLine != null -> GameStatus.WIN
            drop.board.isFull() -> GameStatus.DRAW
            else -> GameStatus.PLAYING
        }
        val winner = if (nextStatus == GameStatus.WIN) currentPlayer else null
        return copy(
            board = drop.board,
            currentPlayer = if (nextStatus == GameStatus.PLAYING) currentPlayer.opponent() else currentPlayer,
            status = nextStatus,
            winner = winner,
            winningCells = winLine.orEmpty(),
            lastMove = Cell(drop.row, drop.column),
            moveCount = moveCount + 1,
            score = winner?.let { score.increment(it) } ?: score
        )
    }

    /** Starts a new round, alternating who plays first to keep things fair. */
    fun newRound(newConfig: GameConfig = config): GameState {
        val nextStarter = if (moveCount == 0 && status == GameStatus.PLAYING) starter else starter.opponent()
        return initial(newConfig, nextStarter).copy(score = score)
    }

    companion object {
        fun initial(config: GameConfig = GameConfig.Classic, starter: Player = Player.RED): GameState =
            GameState(
                config = config,
                board = Board(config.rows, config.columns),
                currentPlayer = starter,
                status = GameStatus.PLAYING,
                starter = starter
            )
    }
}
