package dev.vaibhav.quizzify.ui.screens.gameScreens.game

import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.util.ErrorType

sealed class GameScreenEvents {
    data class ShowToast(val message: String) : GameScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : GameScreenEvents()
    data class NavigateToWaitingScreen(val gameId: String) : GameScreenEvents()
    data class NavigateToGameCompleteScreen(val game: Game) : GameScreenEvents()
    object Vibrate : GameScreenEvents()
    object NavigateBack : GameScreenEvents()
}
