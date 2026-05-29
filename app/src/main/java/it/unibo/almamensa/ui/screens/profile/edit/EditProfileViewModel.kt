package it.unibo.almamensa.ui.screens.profile.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.exceptions.RestException
import it.unibo.almamensa.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileState(
    val name: String = "",
    val surname: String = "",
    val profilePhotoUrl: String? = null,
    val imageVersion: Long = 0,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.myProfile.collect { (user, imageVersion) ->
                user?.let {
                    _state.update { s -> s.copy(
                        name = it.name,
                        surname = it.surname,
                        profilePhotoUrl = it.profilePhotoUrl,
                        imageVersion = imageVersion
                    )}
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val user = userRepository.getMyProfile()
                user?.let { u ->
                    _state.update { s ->
                        s.copy(
                            name = u.name,
                            surname = u.surname,
                            profilePhotoUrl = u.profilePhotoUrl,
                            // Force image reload only if the photo is changed
                            imageVersion = if (u.profilePhotoUrl != s.profilePhotoUrl) {
                                System.currentTimeMillis()
                            } else {
                                s.imageVersion
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Impossibile caricare il profilo")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name, isSaved = false)
    }

    fun onSurnameChange(surname: String) {
        _state.value = _state.value.copy(surname = surname, isSaved = false)
    }

    fun saveProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isSaved = false)
            try {
                userRepository.updateUser(
                    name = _state.value.name,
                    surname = _state.value.surname
                )
                _state.value = _state.value.copy(isSaved = true)
            } catch (e: RestException) {
                _state.value = _state.value.copy(errorMessage = e.error)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Errore di rete")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun uploadProfilePhoto(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                userRepository.uploadProfilePicture(uri)
            } catch (e: RestException) {
                _state.value = _state.value.copy(errorMessage = e.error)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Errore durante il caricamento dell'immagine")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun deleteProfilePhoto() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                userRepository.deleteProfilePicture()
            } catch (e: RestException) {
                _state.value = _state.value.copy(errorMessage = e.error)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Errore durante la rimozione dell'immagine")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}