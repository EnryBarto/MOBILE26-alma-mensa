package it.unibo.almamensa.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.repositories.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val canteenRepository: CanteenRepository
) : ViewModel() {
    private val _canteens = MutableStateFlow<List<Canteen>>(emptyList())
    val canteens: StateFlow<List<Canteen>> = _canteens

    init {
        loadCanteens()
    }

    private fun loadCanteens() {
        viewModelScope.launch {
            _canteens.value = canteenRepository.getAllCanteen()
        }
    }
}
