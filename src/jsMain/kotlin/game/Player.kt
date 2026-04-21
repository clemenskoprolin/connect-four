package game

import kotlinx.serialization.Serializable

@Serializable
enum class Player {
    RED, YELLOW;

    val label: String get() = when (this) { RED -> "Red"; YELLOW -> "Yellow" }
    val cssClass: String get() = when (this) { RED -> "red"; YELLOW -> "yellow" }

    fun opponent(): Player = if (this == RED) YELLOW else RED
}
