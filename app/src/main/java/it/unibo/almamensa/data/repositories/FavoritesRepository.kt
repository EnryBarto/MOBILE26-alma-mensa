package it.unibo.almamensa.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface FavoritesRepository {
    val favoriteIds: Flow<Set<String>>
    suspend fun toggleFavorite(canteenId: Long): Boolean
}

// Implemented using DataStore, so the favorites are saved into the Device
class FavoritesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : FavoritesRepository {

    companion object {
        private val FAVORITES_KEY = stringSetPreferencesKey("favorite_canteen_ids")
    }

    // Retrieve the ID (as string) of the favorites canteen.
    override val favoriteIds: Flow<Set<String>> = dataStore.data
        .map { preferences -> preferences[FAVORITES_KEY] ?: emptySet() }

    override suspend fun toggleFavorite(canteenId: Long): Boolean {
        var hasBeenAdded = false
        dataStore.edit { preferences ->
            val current = preferences[FAVORITES_KEY] ?: emptySet()
            val idStr = canteenId.toString()
            if (current.contains(idStr)) {
                preferences[FAVORITES_KEY] = current - idStr
            } else {
                preferences[FAVORITES_KEY] = current + idStr
                hasBeenAdded = true
            }
        }
        return hasBeenAdded
    }
}