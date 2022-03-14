package dev.vaibhav.quizzify.ui.screens.gameScreens.waitingForPlayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.local.PlayerItem
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
class WaitingForPlayerViewModel @Inject constructor(
    private val gameUseCase: GameUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaitingForPlayerScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<WaitingForPlayerScreenEvents>()
    val events = _events.asSharedFlow()

    private val userId = authRepository.currentUserId!!

    private val gameCode = MutableStateFlow<String?>(null)

    private val game = MutableStateFlow<Game?>(null)

    init {
        collectGameCode()
        collectGame()
    }

    fun setGameCode(code: String) = viewModelScope.launch {
        gameCode.emit(code)
        observeGame(code)
        connectToGame(code)
    }

    fun onStartGamePressed() = viewModelScope.launch {
        gameCode.value?.let { startGame(it) }
    }

    fun isUserTheHost() = userId == game.value?.hostId

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

    private fun collectGame() = viewModelScope.launch {
        game.collectLatest {
            it?.let { game -> handleGame(game) }
        }
    }

    private fun collectGameCode() = viewModelScope.launch {
        gameCode.collectLatest { code ->
            code?.let {
                _uiState.emit(uiState.value.copy(inviteCode = it))
            }
        }
    }

    private fun observeGame(gameId: String) = viewModelScope.launch {
        gameUseCase.observeGame(gameId).collectLatest {
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> it.data?.let { data -> game.emit(data) }
            }
        }
    }

    private suspend fun handleGame(game: Game) {
        val isButtonVisible = game.hostId == userId
        val players = game.players.map { PlayerItem(it, it.playerId == userId) }
        _uiState.update {
            it.copy(players = players, isStartGameButtonVisible = isButtonVisible)
        }
        when (game.gameState) {
            GameState.STARTED.name ->
                _events.emit(WaitingForPlayerScreenEvents.NavigateToGameScreen(game.gameId))
            GameState.CANCELED.name -> {
                if (!isUserTheHost())
                    _events.emit(WaitingForPlayerScreenEvents.ShowToast("Game has been cancelled"))
                _events.emit(WaitingForPlayerScreenEvents.NavigateBack)
            }
        }
    }

    private suspend fun startGame(gameId: String) {
        gameUseCase.startGame(gameId).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(WaitingForPlayerScreenEvents.ShowToast(it.message))
            }
        }
    }

    private suspend fun connectToGame(gameId: String) {
        gameUseCase.connectToGame(gameId, authRepository.getUserData()).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(WaitingForPlayerScreenEvents.ShowToast(it.message))
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
                    _events.emit(WaitingForPlayerScreenEvents.ShowToast(it.message))
                    _events.emit(WaitingForPlayerScreenEvents.NavigateBack)
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
                is Resource.Success -> _events.emit(WaitingForPlayerScreenEvents.ShowToast(it.message))
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) WaitingForPlayerScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else WaitingForPlayerScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}