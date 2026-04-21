package persistence

import game.GameState
import kotlinx.browser.window
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object GameStorage {
    private const val KEY = "connect-four/game-state/v1"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun save(state: GameState) {
        runCatching {
            window.localStorage.setItem(KEY, json.encodeToString(GameState.serializer(), state))
        }
    }

    fun load(): GameState? {
        val raw = runCatching { window.localStorage.getItem(KEY) }.getOrNull() ?: return null
        return try {
            json.decodeFromString(GameState.serializer(), raw)
        } catch (_: SerializationException) {
            clear(); null
        } catch (_: IllegalArgumentException) {
            clear(); null
        }
    }

    fun clear() {
        runCatching { window.localStorage.removeItem(KEY) }
    }
}
