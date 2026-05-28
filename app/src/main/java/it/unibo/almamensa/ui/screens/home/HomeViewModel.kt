package it.unibo.almamensa.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.almamensa.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val userName: String? = null,
) {
    val title: String
        get() = if (userName != null) "Bentornato $userName!" else "Benvenuto!"
}

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.myProfile.collect { userWithVersion ->
                _state.update { it.copy(userName = userWithVersion.user?.name) }
            }
        }
    }
}