package dev.vaibhav.quizzify.data.models.remote.game

data class Player(
    val playerId: String = "",
    val playerName: String = "",
    val playerImg: String = "",
    val solved: Int = 0,
    val hasCompleted: Boolean = false
)