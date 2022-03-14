package dev.vaibhav.quizzify.ui.screens.main.home

import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.util.ErrorType

sealed class HomeScreenEvents {
    data class ShowToast(val message: String) : HomeScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : HomeScreenEvents()
    data class NavigateToQuizDetails(val quiz: QuizDto) : HomeScreenEvents()
    data class NavigateToWaitingForPlayersScreen(val gameId: String) : HomeScreenEvents()
    object OpenInviteCodeDialog : HomeScreenEvents()
    data class OpenQuestionCountDialog(val onCountSelected: (Int) -> Unit) : HomeScreenEvents()
}
