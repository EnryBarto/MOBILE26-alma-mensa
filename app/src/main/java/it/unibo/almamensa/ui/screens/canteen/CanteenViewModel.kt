package it.unibo.almamensa.ui.screens.canteen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.data.local.FavoritesManager
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.model.dto.ReviewWithUserDto
import it.unibo.almamensa.data.repositories.AuthRepository
import it.unibo.almamensa.data.repositories.CanteenRepository
import it.unibo.almamensa.data.repositories.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class CanteenState(
    val canteen: Canteen? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val reviews: List<ReviewWithUserDto> = emptyList(),
    val isLoggedIn: Boolean = false
)

class CanteenViewModel(
    private val canteenId: Long,
    private val canteenRepository: CanteenRepository,
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository,
    private val favoritesManager: FavoritesManager
) : ViewModel() {

    private val _state = MutableStateFlow(CanteenState())
    val state: StateFlow<CanteenState> = _state.asStateFlow()

    init {
        loadCanteenDetails()
        loadCanteenReviews()
        observeAuthStatus()
    }

    fun toggleFavorite(canteenId: Long) {
        viewModelScope.launch {
            val newFavoriteStatus = favoritesManager.toggleFavorite(canteenId)
            _state.update { currentState ->
                currentState.copy(isFavorite = newFavoriteStatus)
            }
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            try {
                reviewRepository.deleteReview(reviewId)
                loadCanteenReviews()
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    private fun observeAuthStatus() {
        viewModelScope.launch {
            authRepository.sessionStatus().collect { status ->
                _state.update { it.copy(
                    isLoggedIn = status is SessionStatus.Authenticated
                ) }
            }
        }
    }

    private fun loadCanteenDetails() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val canteen = canteenRepository.getCanteenById(canteenId)
                _state.value = _state.value.copy(canteen = canteen)
                checkIfFavorite(canteenId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private fun checkIfFavorite(id: Long) {
        viewModelScope.launch {
            favoritesManager.favoriteIds.collect { favoriteSet ->
                val isFav = favoriteSet.contains(id.toString())
                _state.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    fun refresh() {
        loadCanteenReviews()
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
