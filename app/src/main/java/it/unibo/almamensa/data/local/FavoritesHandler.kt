package it.unibo.almamensa.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "favorites_prefs")

class FavoritesManager(private val context: Context) {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_canteen_ids")

    // Retrieve the ID (as string) of the favorites canteen.
    val favoriteIds: Flow<Set<String>> = context.dataStore.data
        .map { preferences -> preferences[FAVORITES_KEY] ?: emptySet() }

    suspend fun toggleFavorite(canteenId: Long) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITES_KEY] ?: emptySet()
            val idStr = canteenId.toString()
            if (current.contains(idStr)) {
                preferences[FAVORITES_KEY] = current - idStr
            } else {
                preferences[FAVORITES_KEY] = current + idStr
            }
        }
    }
}