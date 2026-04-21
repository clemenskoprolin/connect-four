package ui

import kotlinx.browser.document

enum class Theme(val id: String) {
    Dark("dark"),
    Light("light");

    fun opposite(): Theme = if (this == Dark) Light else Dark

    companion object {
        fun fromId(id: String?): Theme? = entries.firstOrNull { it.id == id }
    }
}

fun applyTheme(theme: Theme) {
    document.documentElement?.setAttribute("data-theme", theme.id)
    document
        .querySelector("meta[name='theme-color']")
        ?.setAttribute("content", if (theme == Theme.Dark) "#0a0e27" else "#f8fafc")
}
