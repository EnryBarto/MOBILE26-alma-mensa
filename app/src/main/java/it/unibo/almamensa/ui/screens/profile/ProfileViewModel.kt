package it.unibo.almamensa.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.data.model.User
import it.unibo.almamensa.data.repositories.AuthRepository
import it.unibo.almamensa.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.sessionStatus().collect { status ->
                if (status is SessionStatus.Authenticated) {
                    loadProfile(status.session.user?.id ?: return@collect)
                }
            }
        }
    }

    private suspend fun loadProfile(userId: String) {
        _state.value = _state.value.copy(isLoading = true)
        try {
            val user = userRepository.getProfile(userId)
            _state.value = _state.value.copy(user = user)
        } catch (e: Exception) {
            _state.value = _state.value.copy(errorMessage = "Errore nel caricamento del profilo")
        } finally {
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}