package dev.vaibhav.quizzify.data.remote.user

import dev.vaibhav.quizzify.data.models.remote.UserDto
import dev.vaibhav.quizzify.util.Resource

interface UserDataSource {

    suspend fun getUserData(userId: String): Resource<UserDto>

    suspend fun saveUserData(userDto: UserDto): Resource<Unit>

    suspend fun updateAvatar(userId: String, avatar: String): Resource<Unit>
}