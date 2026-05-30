package it.unibo.almamensa.ui.screens.nearme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.CanteenDistance
import it.unibo.almamensa.data.repositories.CanteenRepository
import it.unibo.almamensa.data.repositories.DistanceRepository
import it.unibo.almamensa.data.repositories.LocationRepository
import it.unibo.almamensa.ui.model.CanteenListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NearMeState(
    val canteens: List<CanteenDistance> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val maxDistanceKm: Float = 2f,
    val showLocationDisabledAlert: Boolean = false,
    val showPermissionDeniedAlert: Boolean = false
) {
    val canteenListItems: List<CanteenListItem>
        get() = canteens.map { item ->
            val km = "%.1f km".format(item.distanceMeters / 1000)
            val min = (item.durationSeconds / 60).toInt()
            CanteenListItem(
                canteen = item.canteen,
                distanceInfo = "$km · ${if (min >= 60) "%.1f ore".format(min.toDouble() / 60) else "$min min"} a piedi"
            )
        }
}

class NearMeViewModel(
    private val canteenRepository: CanteenRepository,
    private val locationRepository: LocationRepository,
    private val distanceRepository: DistanceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NearMeState())
    val state = _state.asStateFlow()

    private var allCanteens: List<CanteenDistance> = emptyList()

    fun setMaxDistance(km: Float) {
        _state.update { canteen ->
            canteen.copy(
            maxDistanceKm = km,
            canteens = allCanteens.filter { it.distanceMeters / 1000 <= km }
        )}
    }
    fun loadNearbyCanteens(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true) }
            } else {
                _state.update { it.copy(isLoading = true) }
            }
            _state.update { it.copy(errorMessage = null) }

            try {
                val canteens = canteenRepository.getAllCanteen()
                val location = locationRepository.getCurrentLocation()

                if (location == null) {
                    _state.update { it.copy(showPermissionDeniedAlert = true) }
                    return@launch
                }

                val sorted = distanceRepository.getDistances(
                    location.latitude, location.longitude, canteens
                ).sortedBy { if (it.distanceMeters < 0) Double.MAX_VALUE else it.distanceMeters }
                allCanteens = sorted
                _state.update {
                    it.copy(
                        canteens = sorted.filter { c -> c.distanceMeters / 1000 <= _state.value.maxDistanceKm }
                    )
                }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(showLocationDisabledAlert = true) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false, isRefreshing = false) }
            }
        }
    }

    fun dismissLocationDisabledAlert() = _state.update { it.copy(showLocationDisabledAlert = false) }
    fun dismissPermissionDeniedAlert() = _state.update { it.copy(showPermissionDeniedAlert = false) }
}