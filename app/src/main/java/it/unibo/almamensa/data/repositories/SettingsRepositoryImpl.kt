package it.unibo.almamensa.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import it.unibo.almamensa.data.model.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository {
    val theme: Flow<Theme>
    val dynamicColor: Flow<Boolean>
    suspend fun setTheme(theme: Theme)
    suspend fun setDynamicColor(enabled: Boolean)
}

// Implemented using DataStore, so the theme is saved into the Device
class SettingsRepositoryImpl (
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamicColor")
    }

    override val theme = dataStore.data.map { preferences ->
        try {
            Theme.valueOf(preferences[THEME_KEY] ?: "Sistema")
        } catch (_: Exception) {
            Theme.Sistema
        }
    }

    override val dynamicColor = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR_KEY] ?: false
    }

    override suspend fun setTheme(theme: Theme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.toString()
        }
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = enabled
        }
    }
}
