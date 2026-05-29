package it.unibo.almamensa.ui.screens.profile.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.data.model.User
import it.unibo.almamensa.data.repositories.AuthRepository
import it.unibo.almamensa.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val imageVersion: Long = 0
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
                    userRepository.myProfile.collect { (user, imageVersion) ->
                        _state.update { it.copy(user = user, imageVersion = imageVersion) }
                    }
                } else {
                    _state.value = ProfileState()
                }
            }
        }
    }
}