package dev.vaibhav.quizzify.ui.screens.quizScreens.quizDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.data.models.remote.UserDto
import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.ui.usecases.GameUseCase
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.toGame
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizDetailViewModel @Inject constructor(
    private val gameUseCase: GameUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizDetailScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<QuizDetailScreenEvents>()
    val events = _events.asSharedFlow()

    private val quiz = MutableStateFlow<QuizDto?>(null)

    private val userId = authRepository.currentUserId!!

    init {
        collectQuiz()
    }

    fun setData(quizDto: QuizDto) = viewModelScope.launch {
        quiz.emit(quizDto)
    }

    private fun collectQuiz() = viewModelScope.launch {
        quiz.collectLatest {
            it?.let { quiz ->
                _uiState.update { state ->
                    state.copy(
                        title = quiz.name.ifEmpty { quiz.category.name },
                        description = quiz.description.ifEmpty { "This quiz is made to test your knowledge on ${quiz.category.name}" },
                        image = quiz.category.image,
                        questionCount = quiz.questionCount.toString(),
                        categoryName = quiz.category.name
                    )
                }
            }
        }
    }

    fun onPlaySoloPressed() = viewModelScope.launch {
        quiz.value?.let {
            val game = it.toGame(isSoloGame = true, hostId = userId)
            createGame(game)
            connectToGame(game.gameId, authRepository.getUserData())
            _events.emit(QuizDetailScreenEvents.NavigateToGameScreen(gameId = game.gameId))
        }
    }

    fun onPlayWithFriendsPressed() = viewModelScope.launch {
        quiz.value?.let {
            val game = it.toGame(isSoloGame = false, hostId = userId)
            createGame(game)
            _events.emit(
                QuizDetailScreenEvents.NavigateToWaitingForPlayerScreen(game.gameId)
            )
        }
    }

    private suspend fun createGame(game: Game) {
        gameUseCase.createGame(game).collectLatest {
            _uiState.update { state -> state.copy(isLoading = it is Resource.Loading) }
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(QuizDetailScreenEvents.ShowToast(it.message))
            }
        }
    }

    private suspend fun connectToGame(gameId: String, userDto: UserDto) {
        gameUseCase.connectToGame(gameId, userDto).collectLatest {
            _uiState.update { state -> state.copy(isLoading = it is Resource.Loading) }
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(QuizDetailScreenEvents.ShowToast(it.message))
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) QuizDetailScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else QuizDetailScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}