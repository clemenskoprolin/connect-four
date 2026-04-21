package game

import kotlinx.serialization.Serializable

@Serializable
data class Cell(val row: Int, val column: Int)
