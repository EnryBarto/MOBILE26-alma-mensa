package it.unibo.almamensa.ui.screens.canteen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.model.Review
import it.unibo.almamensa.data.repositories.CanteenRepository
import it.unibo.almamensa.data.repositories.ReviewRepository
import it.unibo.almamensa.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


data class ReviewWithName(
        val review: Review,
        val name: String,
        val surname: String
)

data class CanteenState(
    val canteen: Canteen? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val reviews: List<ReviewWithName> = emptyList()
)

class CanteenViewModel(
    private val canteenId: Long,
    private val canteenRepository: CanteenRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
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
                val reviews = reviewRepository.getReviewsByCanteenId(canteenId)

                // Start all profile requests in parallel using async
                val deferredReviews = reviews.map { review ->
                    async {
                        val profile = userRepository.getProfile(review.userId)
                        ReviewWithName(
                            review = review,
                            name = profile?.name ?: "Anonymous",
                            surname = profile?.surname ?: ""
                        )
                    }
                }
                val reviewsWithNames = deferredReviews.awaitAll()

                _state.value = _state.value.copy(reviews = reviewsWithNames)

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
