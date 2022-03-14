package dev.vaibhav.quizzify.ui.screens.gameScreens.waitingForFinish

import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.util.ErrorType

sealed class WaitingForFinishEvents {
    data class ShowToast(val message: String) : WaitingForFinishEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : WaitingForFinishEvents()
    data class NavigateToGameCompleteScreen(val game: Game) : WaitingForFinishEvents()
    object NavigateBack : WaitingForFinishEvents()
}