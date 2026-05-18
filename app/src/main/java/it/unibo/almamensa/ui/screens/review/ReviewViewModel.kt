package it.unibo.almamensa.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.data.model.Review
import it.unibo.almamensa.data.repositories.AuthRepository
import it.unibo.almamensa.data.repositories.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ReviewState(
    val title: String = "",
    val score: Int = 5,
    val description: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val isEditing: Boolean = false
)

class ReviewViewModel(
    private val canteenId: Long?,
    private val reviewId: Long?,
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewState(isEditing = reviewId != null))
    val state: StateFlow<ReviewState> = _state.asStateFlow()

    init {
        if (reviewId != null) {
            loadReview(reviewId)
        }
    }

    private fun loadReview(id: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val review = reviewRepository.getReviewById(id)
                _state.value = _state.value.copy(
                    title = review.title,
                    score = review.score,
                    description = review.description ?: "",
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Errore nel caricamento della recensione: ${e.message}"
                )
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _state.value = _state.value.copy(title = newTitle)
    }

    fun onScoreChange(newScore: Int) {
        _state.value = _state.value.copy(score = newScore)
    }

    fun onDescriptionChange(newDescription: String) {
        _state.value = _state.value.copy(description = newDescription)
    }

    fun submitReview() {
        val currentState = _state.value
        if (currentState.title.isBlank()) {
            _state.value = currentState.copy(errorMessage = "Il titolo non può essere vuoto")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val sessionStatus = authRepository.sessionStatus().first()
                if (sessionStatus is SessionStatus.Authenticated) {
                    val userId = sessionStatus.session.user?.id ?: throw Exception("Utente non autenticato")
                    
                    if (reviewId != null) {
                        // Update existing review
                        val existingReview = reviewRepository.getReviewById(reviewId)
                        val updatedReview = Review(
                            id = reviewId,
                            score = currentState.score,
                            description = currentState.description,
                            title = currentState.title,
                            canteenId = existingReview.canteenId,
                            userId = userId
                        )
                        reviewRepository.updateReview(reviewId, updatedReview)
                    } else if (canteenId != null) {
                        // Insert new review
                        val review = Review(
                            score = currentState.score,
                            description = currentState.description,
                            title = currentState.title,
                            canteenId = canteenId,
                            userId = userId
                        )
                        reviewRepository.insertReview(review)
                    } else {
                        throw Exception("Dati mancanti per l'operazione")
                    }
                    
                    _state.value = _state.value.copy(isSuccess = true)
                } else {
                    _state.value = _state.value.copy(errorMessage = "Devi essere loggato per lasciare una recensione")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Errore durante il salvataggio: ${e.message}")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
