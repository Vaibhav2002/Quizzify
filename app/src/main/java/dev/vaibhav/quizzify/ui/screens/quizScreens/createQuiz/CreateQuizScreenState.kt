package dev.vaibhav.quizzify.ui.screens.quizScreens.createQuiz

import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuestionDto

data class CreateQuizScreenState(
    val name: String = "",
    val description: String = "",
    val category: CategoryDto? = null,
    val questions: List<QuestionDto> = emptyList(),
    val isLoading: Boolean = false
) {
    val isSaveButtonEnabled: Boolean
        get() = name.isNotEmpty() && description.isNotEmpty() && category != null && questions.isNotEmpty() && !isLoading
}
