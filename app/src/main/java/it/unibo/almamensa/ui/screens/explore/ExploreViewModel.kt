package it.unibo.almamensa.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.local.FavoritesManager
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.repositories.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init

data class ExploreState(
    val canteens: List<Canteen> = emptyList(),
    val allCanteens: List<Canteen> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExploreViewModel(
    private val canteenRepository: CanteenRepository,
    private val favoritesManager: FavoritesManager
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()
    private var favoriteIds = setOf<String>()

    init {
        viewModelScope.launch {
            favoritesManager.favoriteIds.collect { ids ->
                favoriteIds = ids
            }
        }
    }

    fun loadCanteens(isFavoritesOnly: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                var canteens = canteenRepository.getAllCanteen()

                // filters only the canteen's ID which are in the DataStore
                if (isFavoritesOnly) {
                    canteens = canteens.filter { favoriteIds.contains(it.id.toString()) }
                }

                _state.update { it.copy(allCanteens = canteens, canteens = canteens) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleFavorite(canteenId: Long) {
        viewModelScope.launch {
            favoritesManager.toggleFavorite(canteenId)
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(
            searchQuery = query,
            canteens = if (query.isBlank()) it.allCanteens
            else it.allCanteens.filter { canteen ->
                canteen.name.contains(query, ignoreCase = true) ||
                        canteen.address.contains(query, ignoreCase = true) ||
                        canteen.description?.contains(query, ignoreCase = true) == true
            }
        )}
    }
}