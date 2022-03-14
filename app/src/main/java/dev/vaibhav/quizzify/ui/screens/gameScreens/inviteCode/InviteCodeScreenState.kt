package dev.vaibhav.quizzify.ui.screens.gameScreens.inviteCode

import dev.vaibhav.quizzify.util.Constants.GAME_KEY_LENGTH

data class InviteCodeScreenState(
    val inviteCode: String = "",
    val isLoading: Boolean = false,

    val inviteCodeError: String? = null
) {
    val isButtonEnabled = inviteCode.length == GAME_KEY_LENGTH && !isLoading
}