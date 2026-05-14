package it.unibo.almamensa.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.repositories.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapState(
    val canteens: List<Canteen> = emptyList()
)

class MapViewModel(
    private val canteenRepository: CanteenRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(canteens = canteenRepository.getAllCanteen()) }
        }
    }
}