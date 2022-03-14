package dev.vaibhav.quizzify.ui.screens.quizScreens.createQuiz

import dev.vaibhav.quizzify.data.models.remote.QuestionDto
import dev.vaibhav.quizzify.util.ErrorType

sealed class CreateQuizScreenEvents {
    data class ShowToast(val message: String) : CreateQuizScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : CreateQuizScreenEvents()
    data class OpenAddQuestionDialog(val onQuestionAdded: (QuestionDto) -> Unit) :
        CreateQuizScreenEvents()

    object NavigateBack : CreateQuizScreenEvents()
}
