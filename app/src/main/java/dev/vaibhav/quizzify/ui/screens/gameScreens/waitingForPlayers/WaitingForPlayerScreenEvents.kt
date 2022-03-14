package dev.vaibhav.quizzify.ui.screens.gameScreens.waitingForPlayers

import dev.vaibhav.quizzify.util.ErrorType

sealed class WaitingForPlayerScreenEvents {
    data class ShowToast(val message: String) : WaitingForPlayerScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : WaitingForPlayerScreenEvents()
    data class NavigateToGameScreen(val gameId: String) : WaitingForPlayerScreenEvents()
    object NavigateBack : WaitingForPlayerScreenEvents()
}