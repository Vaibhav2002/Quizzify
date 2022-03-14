package dev.vaibhav.quizzify.ui.screens.quizScreens.addQuestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.models.remote.OptionDto
import dev.vaibhav.quizzify.data.models.remote.QuestionDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddQuestionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AddQuestionScreenState())
    val uiState = _uiState.asStateFlow()

    fun onQuestionChanged(question: String) = viewModelScope.launch {
        _uiState.update { it.copy(question = question) }
    }

    fun onOptionChanged(optionIndex: Int, option: String) = viewModelScope.launch {
        val newOptions = uiState.value.options.toMutableList().apply {
            set(optionIndex, option)
        }
        _uiState.update { it.copy(options = newOptions) }
    }

    fun onAnswerChanged(answer: String) = viewModelScope.launch {
        _uiState.update { it.copy(answer = answer) }
    }

    fun getQuestions(): QuestionDto {
        val state = uiState.value
        return QuestionDto(
            question = state.question,
            options = state.options.map {
                OptionDto(it, correct = it == state.answer)
            }
        )
    }
}