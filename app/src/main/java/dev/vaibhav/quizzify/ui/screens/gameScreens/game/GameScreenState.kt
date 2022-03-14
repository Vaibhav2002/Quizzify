package dev.vaibhav.quizzify.ui.screens.gameScreens.game

import dev.vaibhav.quizzify.data.models.local.OptionItem
import dev.vaibhav.quizzify.util.Constants.GAME_TIME

data class GameScreenState(
    val title: String = "",
    val questionNo: String = "",
    val question: String = "",
    val questionCount: String = "",
    val rank: String = "",
    val timeLeft: Long = 0L,
    val options: List<OptionItem> = emptyList(),
    val isResultsShowing: Boolean = false,
    val isLoading: Boolean = false,
) {
    val timeLeftText: String
        get() = "${timeLeft / 1000} sec left"

    val progress: Double
        get() = timeLeft.toDouble() / GAME_TIME * 100

    val isButtonEnabled: Boolean
        get() = options.any { it.isSelected } && !isLoading && !isResultsShowing

    val questionCountText: String
        get() = "Q $questionNo / $questionCount"

    val rankText: String
        get() = "Rank $rank"
}