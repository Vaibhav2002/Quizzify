package dev.vaibhav.quizzify.ui.screens.gameScreens.winning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.Player
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.data.repo.quiz.QuizRepo
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WinningViewModel @Inject constructor(
    private val quizRepo: QuizRepo,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WinningScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<WinningScreenEvents>()
    val events = _events.asSharedFlow()

    private val game = MutableStateFlow<Game?>(null)

    init {
        collectGame()
    }

    fun setGame(gameData: Game) = viewModelScope.launch {
        game.emit(gameData)
    }

    fun onUpvoteButtonPressed() = viewModelScope.launch {
        upvoteQuiz()
    }

    fun onHomeButtonPressed() = viewModelScope.launch {
        _events.emit(WinningScreenEvents.NavigateToHome)
    }

    fun onRankingButtonPressed() = viewModelScope.launch {
        game.value?.let { _events.emit(WinningScreenEvents.OpenRankingBottomSheet(it)) }
    }

    private fun collectGame() = viewModelScope.launch {
        game.collectLatest { game ->
            game?.let {
                val user = authRepository.getUserData()
                _uiState.update { state ->
                    state.copy(
                        profilePic = user.profilePic,
                        rank = getUserRank(user.userId, it.players),
                        isUpvoteButtonEnabled = !it.quiz.isInstantQuiz
                    )
                }
            }
        }
    }

    private fun getUserRank(userId: String, players: List<Player>): Int {
        val rank = players.sortedByDescending { it.solved }.indexOfFirst { it.playerId == userId }
        return rank + 1
    }

    private suspend fun upvoteQuiz() {
        quizRepo.upvoteQuiz(game.value!!.quiz).collectLatest {
            _uiState.emit(
                uiState.value.copy(
                    isLoading = it is Resource.Loading,
                    isUpvoteButtonEnabled = it is Resource.Error
                )
            )
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(WinningScreenEvents.ShowToast(it.message))
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) WinningScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else WinningScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}