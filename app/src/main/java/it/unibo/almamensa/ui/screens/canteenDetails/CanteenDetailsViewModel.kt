package it.unibo.almamensa.ui.screens.canteenDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.repositories.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CanteenDetailsState(
    val canteen: Canteen? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CanteenDetailsViewModel(
    private val canteenId: Long,
    private val canteenRepository: CanteenRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CanteenDetailsState())
    val state: StateFlow<CanteenDetailsState> = _state.asStateFlow()

    init {
        loadCanteenDetails()
    }

    private fun loadCanteenDetails() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val canteen = canteenRepository.getCanteenById(canteenId)
                _state.value = _state.value.copy(canteen = canteen)
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
