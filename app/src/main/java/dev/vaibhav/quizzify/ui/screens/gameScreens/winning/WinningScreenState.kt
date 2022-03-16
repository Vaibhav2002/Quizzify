package dev.vaibhav.quizzify.ui.screens.gameScreens.winning

import dev.vaibhav.quizzify.util.getOrdinalString

data class WinningScreenState(
    val profilePic: String = "",
    val rank: Int = 0,
    val isLoading: Boolean = false,
    val isUpvoteButtonVisible: Boolean = true,
    val isUpvoteButtonEnabled: Boolean = true
) {

    val rankString: String
        get() = "You ranked ${rank.getOrdinalString()}"
}