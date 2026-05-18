package it.unibo.almamensa.ui.screens.profile.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import it.unibo.almamensa.data.model.dto.ReviewWithCanteenDto
import it.unibo.almamensa.data.repositories.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PersonalReviewState(
    val reviews: List<ReviewWithCanteenDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUserId: String? = null
)

class PersonalReviewViewModel(
    private val repository: ReviewRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _state = MutableStateFlow(PersonalReviewState())
    val state: StateFlow<PersonalReviewState> = _state.asStateFlow()

    init {
        val user = supabase.auth.currentUserOrNull()
        _state.value = _state.value.copy(currentUserId = user?.id)
        loadPersonalReviews()
    }

    fun loadPersonalReviews() {
        val userId = _state.value.currentUserId ?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Recuperiamo le recensioni dell'utente loggato con JOIN sulle mense
                val reviewsDto = repository.getReviewsByUserWithCanteen(userId= userId)

                _state.value = _state.value.copy(reviews = reviewsDto, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteReview(reviewId)
                loadPersonalReviews() // Ricarica la lista
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Errore durante l'eliminazione")
            }
        }
    }
}