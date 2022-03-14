package dev.vaibhav.quizzify.ui.screens.gameScreens.waitingForFinish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.GameState
import dev.vaibhav.quizzify.data.models.remote.game.Player
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.ui.usecases.GameUseCase
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitingForFinishViewModel @Inject constructor(
    private val gameUseCase: GameUseCase,
    private val authRepository: AuthRepository
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(WaitingForFinishScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<WaitingForFinishEvents>()
    val events = _events.asSharedFlow()

    private val gameId = MutableStateFlow<String?>(null)

    private val game = MutableStateFlow<Game?>(null)

    private val userId = authRepository.currentUserId!!

    init {
        viewModelScope.launch { setUserImage() }
    }

    fun setGameId(id: String) = viewModelScope.launch {
        gameId.emit(id)
        observeGame(id)
    }

    private suspend fun setUserImage() {
        val profilePic = authRepository.getUserData().profilePic
        _uiState.update { it.copy(userImage = profilePic) }
    }

    private fun observeGame(gameId: String) = viewModelScope.launch {
        gameUseCase.observeGame(gameId).collectLatest {
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    it.data?.let { data -> handleGame(data) }
                }
            }
        }
    }

    private suspend fun handleGame(game: Game) {
        this.game.emit(game)
        val event = when (game.gameState) {
            GameState.FINISHED.name -> WaitingForFinishEvents.NavigateToGameCompleteScreen(game)
            GameState.CANCELED.name -> WaitingForFinishEvents.NavigateBack
            else -> null
        }
        event?.let { _events.emit(it) }
    }

    fun isUserTheHost() = game.value!!.hostId == userId

    fun onHostExitDialogConfirmPressed() = viewModelScope.launch {
        game.value?.gameId?.let { cancelGame(it) }
    }

    fun onLeaveDialogConfirmPressed() = viewModelScope.launch {
        game.value?.let { game ->
            game.players.find { it.playerId == userId }?.let {
                leaveGame(it, game)
            }
        }
    }

    private suspend fun leaveGame(player: Player, game: Game) {
        gameUseCase.leaveGame(player, game).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    _events.emit(WaitingForFinishEvents.ShowToast(it.message))
                    _events.emit(WaitingForFinishEvents.NavigateBack)
                }
            }
        }
    }

    private suspend fun cancelGame(gameId: String) {
        gameUseCase.cancelGame(gameId).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(WaitingForFinishEvents.ShowToast(it.message))
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) WaitingForFinishEvents.ShowErrorDialog(
                error.errorType
            )
            else WaitingForFinishEvents.ShowToast(error.message)
        _events.emit(event)
    }
}