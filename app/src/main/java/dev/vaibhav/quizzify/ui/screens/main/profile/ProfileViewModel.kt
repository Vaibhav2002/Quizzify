package dev.vaibhav.quizzify.ui.screens.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.data.repo.game.GameRepo
import dev.vaibhav.quizzify.data.repo.quiz.QuizRepo
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val gameRepo: GameRepo,
    private val authRepository: AuthRepository,
    private val quizRepo: QuizRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileScreenEvents>()
    val events = _events.asSharedFlow()

    val userId = authRepository.currentUserId!!

    init {
        setUserData()
        collectQuizCount()
        viewModelScope.launch {
            getAllGamesOfUser()
        }
    }

    private fun collectQuizCount() = viewModelScope.launch {
        quizRepo.getCountOfQuizzesCreatedByUser(userId).collectLatest { count ->
            _uiState.update { it.copy(quizCount = count) }
        }
    }

    private fun setUserData() = viewModelScope.launch {
        val user = authRepository.getUserData()
        _uiState.update {
            it.copy(userName = user.username, profilePic = user.profilePic)
        }
    }

    private suspend fun getAllGamesOfUser(overrideProgressBar: Boolean = false) {
        gameRepo.getAllGamesOfUser(userId).collectLatest {
            _uiState.emit(
                uiState.value.copy(
                    isLoading = it is Resource.Loading && !overrideProgressBar,
                    isRefreshing = it is Resource.Loading && overrideProgressBar
                )
            )
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> it.data?.let { data -> handleGames(data) }
            }
        }
    }

    private fun handleGames(data: List<Game>) {
        val playedCount = data.size
        val wonCount = data.count { game ->
            val maxSolved = game.players.maxOf { it.solved }
            game.players.find { it.playerId == userId }!!.solved == maxSolved
        }
        val exp = 5 * wonCount
        _uiState.update {
            it.copy(quizzedPlayed = playedCount, quizzesWon = wonCount, exp = exp)
        }
    }

    fun onAboutButtonPressed() = viewModelScope.launch {
        _events.emit(ProfileScreenEvents.NavigateToAboutScreen)
    }

    fun onRefreshed() = viewModelScope.launch {
        getAllGamesOfUser(true)
    }

    fun onLogoutConfirmed() = viewModelScope.launch {
        authRepository.logoutUser()
        _events.emit(ProfileScreenEvents.NavigateToAuthScreen)
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) ProfileScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else ProfileScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}