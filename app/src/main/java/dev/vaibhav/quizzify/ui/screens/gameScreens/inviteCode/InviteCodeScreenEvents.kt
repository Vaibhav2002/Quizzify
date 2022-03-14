package dev.vaibhav.quizzify.ui.screens.gameScreens.inviteCode

import dev.vaibhav.quizzify.util.ErrorType

sealed class InviteCodeScreenEvents {
    data class ShowToast(val message: String) : InviteCodeScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : InviteCodeScreenEvents()
    data class NavigateBack(val gameId: String) : InviteCodeScreenEvents()
}
