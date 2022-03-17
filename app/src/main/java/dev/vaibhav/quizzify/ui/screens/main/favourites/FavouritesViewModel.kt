package dev.vaibhav.quizzify.ui.screens.main.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.data.repo.quiz.QuizRepo
import dev.vaibhav.quizzify.data.repo.user.UserRepo
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val quizRepo: QuizRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouritesScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FavouritesScreenEvents>()
    val events = _events.asSharedFlow()

    private val user =
        userRepo.observeCurrentUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val quizzes = user.flatMapLatest {
        it?.let { quizRepo.getAllFavouriteQuizzes(it.favourites) } ?: emptyFlow()
    }

    init {
        collectQuizzes()
    }

    private fun collectQuizzes() = viewModelScope.launch {
        quizzes.collectLatest { _uiState.emit(uiState.value.copy(quizzes = it)) }
    }

    fun onQuizPressed(quizDto: QuizDto) = viewModelScope.launch {
        _events.emit(FavouritesScreenEvents.NavigateToQuizDetailScreen(quizDto))
    }

    fun onQuizLongPressed(quizDto: QuizDto) = viewModelScope.launch {
        removeFromFavourite(quizDto)
    }

    private suspend fun removeFromFavourite(quizDto: QuizDto) {
        userRepo.removeFavourite(quizId = quizDto.id).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(FavouritesScreenEvents.ShowToast(it.message))
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) FavouritesScreenEvents.ShowErrorDialog(
                error.errorType
            )
            else FavouritesScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}