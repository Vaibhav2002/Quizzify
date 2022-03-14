package dev.vaibhav.quizzify.ui.screens.auth.avatarSelect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.local.AvatarItem
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.util.Constants.avatars
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvatarSelectViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(AvatarSelectScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AvatarSelectScreenEvents>()
    val events = _events.asSharedFlow()

    init {
        setAvatarList()
    }

    private fun setAvatarList() = viewModelScope.launch {
        val avatars = avatars.mapIndexed { index, s ->
            AvatarItem(s, index == 0)
        }
        _uiState.update {
            it.copy(avatars = avatars)
        }
    }

    fun onAvatarSelected(avatar: AvatarItem) = viewModelScope.launch {
        val newList = uiState.value.avatars.map {
            it.copy(isSelected = it.avatar == avatar.avatar)
        }
        _uiState.update {
            it.copy(avatars = newList)
        }
    }

    fun onContinueButtonPressed() = viewModelScope.launch {
        updateUserAvatar()
    }

    private suspend fun updateUserAvatar() {
        val selectedAvatar = uiState.value.avatars.find { it.isSelected } ?: return
        authRepository.updateAvatar(selectedAvatar.avatar).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> {
//                    _events.emit(AvatarSelectScreenEvents.ShowToast(it.message))
                    _events.emit(AvatarSelectScreenEvents.NavigateToHomeScreen)
                }
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) AvatarSelectScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else AvatarSelectScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}