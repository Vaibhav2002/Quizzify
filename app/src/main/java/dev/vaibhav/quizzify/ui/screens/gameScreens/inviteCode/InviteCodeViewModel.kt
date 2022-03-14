package dev.vaibhav.quizzify.ui.screens.gameScreens.inviteCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.repo.game.GameRepo
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteCodeViewModel @Inject constructor(private val gameRepo: GameRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(InviteCodeScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<InviteCodeScreenEvents>()
    val events = _events.asSharedFlow()

    fun onInviteCodeChanged(inviteCode: String) = viewModelScope.launch {
        _uiState.update { it.copy(inviteCode = inviteCode) }
    }

    fun onConnectButtonPressed() = viewModelScope.launch {
        checkGameExists(uiState.value.inviteCode)
    }

    private suspend fun checkGameExists(gameId: String) {
        gameRepo.doesGameExist(gameId).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> handleSuccess(it)
            }
        }
    }

    private suspend fun handleSuccess(resource: Resource<Boolean>) {
        if (resource.data == true)
            _events.emit(InviteCodeScreenEvents.NavigateBack(uiState.value.inviteCode))
        else
            _uiState.update { it.copy(inviteCodeError = resource.message) }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) InviteCodeScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else InviteCodeScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}