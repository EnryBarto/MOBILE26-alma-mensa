package it.unibo.almamensa.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.repositories.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreState(
    val canteens: List<Canteen> = emptyList(),
    val allCanteens: List<Canteen> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExploreViewModel(private val canteenRepository: CanteenRepository) : ViewModel() {

    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    init { loadCanteens() }

    fun loadCanteens() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val canteens = canteenRepository.getAllCanteen()
                _state.update { it.copy(allCanteens = canteens, canteens = canteens) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
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