package dev.vaibhav.quizzify.ui.screens.main.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.data.repo.quiz.QuizRepo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(private val quizRepo: QuizRepo) : ViewModel() {

    private val _events = MutableSharedFlow<CommunityScreenEvents>()
    val events = _events.asSharedFlow()

    val categories =
        quizRepo.allCategories.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val selectedCategory = MutableStateFlow<CategoryDto?>(null)

    private val searchQuery = MutableStateFlow("")

    val quizzes = combine(searchQuery, selectedCategory) { query, category ->
        Pair(query, category)
    }.flatMapLatest {
        quizRepo.getAllQuizzes(it.first, it.second)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onCategorySelected(categoryString: String?) = viewModelScope.launch {
        val category = categories.value.find { it.name == categoryString }
        selectedCategory.emit(category)
    }

    fun onSearchQueryChanged(query: String) = viewModelScope.launch {
        searchQuery.emit(query)
    }

    fun onCreateQuizPressed() = viewModelScope.launch {
        _events.emit(CommunityScreenEvents.NavigateToCreateQuizScreen)
    }

    fun onQuizPressed(quiz: QuizDto) = viewModelScope.launch {
        _events.emit(CommunityScreenEvents.NavigateToQuizDetailsScreen(quiz))
    }
}