package persistence

import kotlinx.browser.window
import ui.Theme

object ThemeStorage {
    private const val KEY = "connect-four/theme/v1"

    fun save(theme: Theme) {
        runCatching { window.localStorage.setItem(KEY, theme.id) }
    }

    fun load(): Theme? {
        val raw = runCatching { window.localStorage.getItem(KEY) }.getOrNull() ?: return null
        return Theme.fromId(raw)
    }
}
