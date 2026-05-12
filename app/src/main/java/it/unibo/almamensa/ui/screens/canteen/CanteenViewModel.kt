package it.unibo.almamensa.ui.screens.canteen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.model.dto.ReviewWithUserDto
import it.unibo.almamensa.data.repositories.CanteenRepository
import it.unibo.almamensa.data.repositories.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class CanteenState(
    val canteen: Canteen? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val reviews: List<ReviewWithUserDto> = emptyList()
)

class CanteenViewModel(
    private val canteenId: Long,
    private val canteenRepository: CanteenRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CanteenState())
    val state: StateFlow<CanteenState> = _state.asStateFlow()

    init {
        loadCanteenDetails()
        loadCanteenReviews()
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

    private fun loadCanteenReviews() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val reviewsWithUsers = reviewRepository.getReviewsWithUser(canteenId)
                _state.value = _state.value.copy(reviews = reviewsWithUsers)

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
