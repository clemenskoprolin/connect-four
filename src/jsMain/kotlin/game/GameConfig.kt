package game

import kotlinx.serialization.Serializable

@Serializable
data class GameConfig(
    val rows: Int,
    val columns: Int,
    val winLength: Int
) {
    init {
        require(rows in MIN_SIZE..MAX_SIZE) { "rows must be in $MIN_SIZE..$MAX_SIZE" }
        require(columns in MIN_SIZE..MAX_SIZE) { "columns must be in $MIN_SIZE..$MAX_SIZE" }
        require(winLength in MIN_WIN..MAX_WIN) { "winLength must be in $MIN_WIN..$MAX_WIN" }
    }

    companion object {
        const val MIN_SIZE = 4
        const val MAX_SIZE = 15
        const val MIN_WIN = 3
        const val MAX_WIN = 10

        val Classic = GameConfig(rows = 6, columns = 7, winLength = 4)
    }
}
