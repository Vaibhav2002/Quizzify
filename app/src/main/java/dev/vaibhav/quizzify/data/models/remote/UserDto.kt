package dev.vaibhav.quizzify.data.models.remote

data class UserDto(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val profilePic: String = "",
    val exp: Int = 0,
)