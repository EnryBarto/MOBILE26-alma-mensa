package it.unibo.almamensa.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.repositories.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExploreState(
    val canteens: List<Canteen> = emptyList(),
    val selectedCanteen: Canteen? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExploreViewModel(private val canteenRepository: CanteenRepository) : ViewModel() {

    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    init {
        loadCanteens()
    }

    fun loadCanteens() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val canteens = canteenRepository.getAllCanteen()
                _state.value = _state.value.copy(canteens = canteens)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}