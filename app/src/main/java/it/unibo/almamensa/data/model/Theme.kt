package it.unibo.almamensa.data.model

enum class Theme {
    SYSTEM, LIGHT, DARK;

    fun label() = when(this) {
        SYSTEM -> "Sistema"
        LIGHT -> "Chiaro"
        DARK -> "Scuro"
    }
}