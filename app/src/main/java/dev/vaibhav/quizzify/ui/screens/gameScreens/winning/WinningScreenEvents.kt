package dev.vaibhav.quizzify.ui.screens.gameScreens.winning

import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.util.ErrorType

sealed class WinningScreenEvents {
    data class ShowToast(val message: String) : WinningScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : WinningScreenEvents()
    data class OpenRankingBottomSheet(val game: Game) : WinningScreenEvents()
    object NavigateToHome : WinningScreenEvents()
    object SHowConfetti : WinningScreenEvents()
}
