package dev.vaibhav.quizzify.ui.screens.main.profile

data class ProfileScreenState(
    val userName: String = "",
    val profilePic: String = "",
    val quizzedPlayed: Int = 0,
    val quizzesWon: Int = 0,
    val exp: Int = 0,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)