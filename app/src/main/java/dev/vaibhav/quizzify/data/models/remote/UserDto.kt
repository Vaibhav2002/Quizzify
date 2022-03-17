package dev.vaibhav.quizzify.data.models.remote

data class UserDto(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val profilePic: String = "",
    val exp: Int = 0,
    val favourites: List<String> = emptyList()
) {

    companion object {
        fun deserializeFavourites(fav: String) = fav.split(",")
        fun serializeFavourites(favourites: List<String>) = favourites.joinToString(",")
    }
}