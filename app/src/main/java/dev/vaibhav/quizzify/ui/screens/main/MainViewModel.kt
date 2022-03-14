package dev.vaibhav.quizzify.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.repo.quiz.QuizRepo
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val quizRepo: QuizRepo
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _events = MutableSharedFlow<MainScreenEvents>()
    val events = _events.asSharedFlow()

    init {
        fetchAllCategories()
        fetchAllQuizzes()
    }

    private fun fetchAllCategories() = viewModelScope.launch {
        quizRepo.fetchAllCategories().collectLatest {
            _isLoading.emit(it is Resource.Loading)
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> Unit
            }
        }
    }

    private fun fetchAllQuizzes() = viewModelScope.launch {
        quizRepo.fetchAllQuizzes().collectLatest {
            _isLoading.emit(it is Resource.Loading)
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> Unit
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) MainScreenEvents.ShowErrorDialog(error.errorType)
            else MainScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}