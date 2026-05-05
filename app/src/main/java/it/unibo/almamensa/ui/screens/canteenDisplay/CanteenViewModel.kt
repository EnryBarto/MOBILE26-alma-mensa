package it.unibo.almamensa.ui.screens.canteenDisplay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.repositories.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CanteenState(
    val canteens: List<Canteen> = emptyList(),
    val selectedCanteen: Canteen? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CanteenViewModel(private val canteenRepository: CanteenRepository) : ViewModel() {

    private val _state = MutableStateFlow(CanteenState())
    val state: StateFlow<CanteenState> = _state.asStateFlow()

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

    fun selectCanteen(canteen: Canteen) {
        _state.value = _state.value.copy(selectedCanteen = canteen)
    }

    fun clearSelectedCanteen() {
        _state.value = _state.value.copy(selectedCanteen = null)
    }
/*
    fun insertCanteen(canteen: Canteen) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                canteenRepository.insertCanteen(canteen)
                loadCanteens()
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun updateCanteen(id: Int, canteen: Canteen) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                canteenRepository.updateCanteen(id, canteen)
                loadCanteens()
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun deleteCanteen(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                canteenRepository.deleteCanteen(id)
                loadCanteens()
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
*/
    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}