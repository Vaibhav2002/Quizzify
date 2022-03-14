package dev.vaibhav.quizzify.ui.screens.quizScreens.quizDetails

import dev.vaibhav.quizzify.util.ErrorType

sealed class QuizDetailScreenEvents {
    data class ShowToast(val message: String) : QuizDetailScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : QuizDetailScreenEvents()
    data class NavigateToGameScreen(val gameId: String) : QuizDetailScreenEvents()
    data class NavigateToWaitingForPlayerScreen(
        val gameId: String
    ) : QuizDetailScreenEvents()
}