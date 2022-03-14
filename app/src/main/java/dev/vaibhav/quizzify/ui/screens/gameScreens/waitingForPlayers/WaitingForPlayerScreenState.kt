package dev.vaibhav.quizzify.ui.screens.gameScreens.waitingForPlayers

import dev.vaibhav.quizzify.data.models.local.PlayerItem

data class WaitingForPlayerScreenState(
    val inviteCode: String = "",
    val players: List<PlayerItem> = emptyList(),
    val isStartGameButtonVisible: Boolean = false,
    val isLoading: Boolean = false
)