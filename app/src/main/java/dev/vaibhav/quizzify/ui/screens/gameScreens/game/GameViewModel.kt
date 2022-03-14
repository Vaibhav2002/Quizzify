package dev.vaibhav.quizzify.ui.screens.gameScreens.game

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.local.OptionItem
import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.GameState
import dev.vaibhav.quizzify.data.models.remote.game.Player
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.ui.usecases.GameUseCase
import dev.vaibhav.quizzify.util.Constants.GAME_TIME
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameUseCase: GameUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameScreenEvents>()
    val events = _events.asSharedFlow()

    private val questionIndex = MutableStateFlow<Int?>(null)

    private val game = MutableStateFlow<Game?>(null)

    private val userId = authRepository.currentUserId!!

    private val gameId = MutableStateFlow<String?>(null)

    private val timer = object : CountDownTimer(GAME_TIME, 1000L) {
        override fun onTick(p0: Long) {
            _uiState.update { it.copy(timeLeft = p0) }
        }

        override fun onFinish() {
            _uiState.update { it.copy(timeLeft = 0L) }
            onTimerEnd()
        }
    }

    init {
        collectGame()
        collectQuestionIndex()
    }

    fun setGameId(gameID: String) = viewModelScope.launch {
        val oldGameId = gameId.value
        gameId.emit(gameID)
        if (oldGameId == null) observeGame(gameID)
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

    private fun collectGame() = viewModelScope.launch {
        game.collectLatest { game ->
            game?.let {
                _uiState.update { state ->
                    state.copy(
                        title = it.quiz.name,
                        rank = getUserRank(it.players).toString(),
                        questionCount = it.quiz.questionCount.toString()
                    )
                }
                val event = when {
                    it.gameState == GameState.FINISHED.name ->
                        GameScreenEvents.NavigateToGameCompleteScreen(it)

                    it.gameState == GameState.CANCELED.name -> GameScreenEvents.NavigateBack
                    getCurrentPlayer().hasCompleted ->
                        GameScreenEvents.NavigateToWaitingScreen(it.gameId)
                    else -> null
                }
                event?.let { _events.emit(it) }
            }
        }
    }

    private fun collectQuestionIndex() = viewModelScope.launch {
        questionIndex.collectLatest { questionIndex ->
            questionIndex?.let { index ->
                if (index >= game.value?.quiz?.questionCount!!) {
                    handleAllQuestionsAnswered()
                } else {
                    val question = game.value!!.quiz.questions[index]
                    _uiState.update {
                        it.copy(
                            questionNo = (index + 1).toString(),
                            options = question.options.map { option ->
                                OptionItem(option)
                            },
                            question = question.question
                        )
                    }
                    restartTimer()
                }
            }
        }
    }

    private fun handleAllQuestionsAnswered() = viewModelScope.launch {
        setGameCompleted(game.value!!, getCurrentPlayer())
        timer.cancel()
    }

    private fun getUserRank(players: List<Player>): Int {
        val rank = players.sortedByDescending { it.solved }.indexOfFirst { it.playerId == userId }
        return rank + 1
    }

    private fun restartTimer() = viewModelScope.launch {
        _uiState.update { it.copy(timeLeft = 0L) }
        timer.cancel()
        timer.start()
    }

    private fun onTimerEnd() = viewModelScope.launch {
        onSubmitButtonPressed()
    }

    fun onSubmitButtonPressed() = viewModelScope.launch {
        val optionSelectedIsCorrect =
            uiState.value.options.find { it.isSelected }?.option?.correct == true
        if (optionSelectedIsCorrect)
            submitAnswer(game.value!!, getCurrentPlayer())
        else handleAnswerSubmitted()
    }

    fun onOptionPressed(option: OptionItem) = viewModelScope.launch {
        val newOptions = uiState.value.options.map {
            it.copy(isSelected = option.option.text == it.option.text)
        }
        _uiState.update { it.copy(options = newOptions) }
    }

    private fun handleAnswerSubmitted() = viewModelScope.launch {
        val options = uiState.value.options.map {
            it.copy(
                showColorAfterSubmit = it.option.correct || it.isSelected
            )
        }
        _uiState.update { it.copy(options = options, isResultsShowing = true) }
        _events.emit(GameScreenEvents.Vibrate)
        delay(1000L)
        _uiState.update { it.copy(isResultsShowing = false) }
        questionIndex.update { it?.plus(1) }
    }

    private fun observeGame(gameId: String) = viewModelScope.launch {
        gameUseCase.observeGame(gameId).collectLatest {
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> it.data?.let { data -> handleGame(data) }
            }
        }
    }

    private fun getCurrentPlayer() = game.value?.players?.find { it.playerId == userId }!!

    private suspend fun handleGame(game: Game) {
        val oldGame = this.game.value
        this.game.emit(game)
        if (oldGame == null)
            questionIndex.emit(0)
    }

    private suspend fun setGameCompleted(game: Game, player: Player) {
        gameUseCase.completeGame(game, player).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(GameScreenEvents.ShowToast(it.message))
            }
        }
    }

    private suspend fun submitAnswer(game: Game, player: Player) {
        gameUseCase.submitAnswer(game, player).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> handleAnswerSubmitted()
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
                    _events.emit(GameScreenEvents.ShowToast(it.message))
                    _events.emit(GameScreenEvents.NavigateBack)
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
                is Resource.Success -> _events.emit(GameScreenEvents.ShowToast(it.message))
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) GameScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else GameScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}