package dev.vaibhav.quizzify.ui.screens.quizScreens.createQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.remote.QuestionDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.data.repo.quiz.QuizRepo
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateQuizViewModel @Inject constructor(
    private val quizRepo: QuizRepo,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateQuizScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateQuizScreenEvents>()
    val events = _events.asSharedFlow()

    val categories =
        quizRepo.allCategories.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onNameChanged(name: String) = viewModelScope.launch {
        _uiState.update { it.copy(name = name) }
    }

    fun onDescriptionChanged(description: String) = viewModelScope.launch {
        _uiState.update { it.copy(description = description) }
    }

    fun onCategoryChanged(categoryName: String) = viewModelScope.launch {
        val category = categories.value.find { it.name == categoryName }
        _uiState.update { it.copy(category = category) }
    }

    fun onAddButtonPressed() = viewModelScope.launch {
        _events.emit(
            CreateQuizScreenEvents.OpenAddQuestionDialog(this@CreateQuizViewModel::onQuestionAdded)
        )
    }

    private fun onQuestionAdded(question: QuestionDto) = viewModelScope.launch {
        val newQuestions = uiState.value.questions.toMutableList().apply { add(question) }
        _uiState.update { it.copy(questions = newQuestions) }
    }

    fun onSaveButtonPressed() = viewModelScope.launch {
        val quiz = getQuizFromScreenState(uiState.value)
        saveQuiz(quiz)
    }

    fun onRemoveQuestionPressed(question: QuestionDto) = viewModelScope.launch {
        val newQuestions = uiState.value.questions.toMutableList().apply {
            remove(question)
        }
        _uiState.update { it.copy(questions = newQuestions) }
    }

    private suspend fun saveQuiz(quizDto: QuizDto) {
        quizRepo.saveNewQuiz(quizDto).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    _events.emit(CreateQuizScreenEvents.ShowToast(it.message))
                    _events.emit(CreateQuizScreenEvents.NavigateBack)
                }
            }
        }
    }

    private suspend fun getQuizFromScreenState(state: CreateQuizScreenState): QuizDto {
        val user = authRepository.getUserData()
        return QuizDto(
            name = state.name,
            description = state.description,
            createdBy = user.username,
            createdByUserId = user.userId,
            category = state.category!!,
            questions = state.questions
        )
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) CreateQuizScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else CreateQuizScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}