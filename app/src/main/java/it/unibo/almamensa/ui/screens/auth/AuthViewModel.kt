package it.unibo.almamensa.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.exceptions.RestException
import it.unibo.almamensa.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sessionStatus: SessionStatus = SessionStatus.NotAuthenticated(isSignOut = false)
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // Check if the user is already logged in
    init {
        viewModelScope.launch {
            authRepository.sessionStatus().collect { status ->
                _state.value = _state.value.copy(sessionStatus = status)
            }
        }
    }
    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun signIn(password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                authRepository.signIn(_state.value.email, password)
            } catch (e: RestException) {
                _state.value = _state.value.copy(errorMessage = e.error)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Errore di rete")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun signUp(password: String, name: String, surname: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                authRepository.signUp(_state.value.email, password, name, surname)
            } catch (e: RestException) {
                _state.value = _state.value.copy(errorMessage = e.error)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Errore di rete")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _state.value = AuthState()
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }

}
