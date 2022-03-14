package dev.vaibhav.quizzify.ui.screens.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.local.dataStore.LocalDataStore
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.data.repo.quiz.QuizRepo
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quizRepo: QuizRepo,
    private val localDataStore: LocalDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeScreenEvents>()
    val events = _events.asSharedFlow()

    init {
        collectCategories()
        collectPopularQuizzes()
//        saveSampleQuizzes()
    }

    private fun collectCategories() = viewModelScope.launch {
        quizRepo.allCategories.collectLatest { categories ->
            _uiState.update { it.copy(categories = categories) }
        }
    }

    private fun collectPopularQuizzes() = viewModelScope.launch {
        quizRepo.getAllQuizzes()
            .map {
                it.sortedByDescending { quiz -> quiz.votes }.take(5)
            }
            .collectLatest { quizzes ->
                _uiState.update { it.copy(quizzes = quizzes) }
            }
    }

    fun onJoinGameButtonPressed() = viewModelScope.launch {
        _events.emit(HomeScreenEvents.OpenInviteCodeDialog)
    }

    fun onInviteCodeSuccess(gameId: String) = viewModelScope.launch {
        _events.emit(HomeScreenEvents.NavigateToWaitingForPlayersScreen(gameId))
    }

    fun onQuizItemPressed(quiz: QuizDto) = viewModelScope.launch {
        _events.emit(HomeScreenEvents.NavigateToQuizDetails(quiz))
    }

    fun onCategoryPressed(categoryDto: CategoryDto) = viewModelScope.launch {
        _events.emit(
            HomeScreenEvents.OpenQuestionCountDialog {
                onQuestionCountSelected(categoryDto, it)
            }
        )
    }

    private fun onQuestionCountSelected(categoryDto: CategoryDto, count: Int) =
        viewModelScope.launch {
            fetchInstantQuiz(categoryDto, count)
        }

    private suspend fun fetchInstantQuiz(categoryDto: CategoryDto, count: Int) =
        viewModelScope.launch {
            quizRepo.fetchInstantQuiz(count, categoryDto).collectLatest {
                _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
                when (it) {
                    is Resource.Error -> handleError(it)
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        it.data?.let { data ->
                            _events.emit(HomeScreenEvents.NavigateToQuizDetails(data))
                        }
                    }
                }
            }
        }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) HomeScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else HomeScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }

    private fun saveSampleQuizzes() = viewModelScope.launch(Dispatchers.IO) {
        val user = localDataStore.getUserData()
        val categories = quizRepo.allCategories.first()
        val questionCounts = listOf(5, 10)
        categories.forEach { category ->
            (0..2).forEach { _ ->
                quizRepo.fetchInstantQuiz(questionCounts.random(), category).collectLatest {
                    Timber.d("Fetching" + it.javaClass.toString())
                    if (it is Resource.Success) {
                        quizRepo.saveNewQuiz(
                            it.data!!.copy(
                                createdBy = user.username,
                                createdByUserId = user.userId,
                            )
                        ).collectLatest {
                            Timber.d("Saving" + it.javaClass.toString())
                        }
                    }
                }
            }
        }
    }
}