package dev.vaibhav.quizzify.data.repo.user

import dev.vaibhav.quizzify.data.models.remote.UserDto
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepo {

    fun observeCurrentUser(): Flow<UserDto>

    suspend fun getCurrentUser(): UserDto

    suspend fun fetchUserData(userId: String): Flow<Resource<UserDto>>

    suspend fun saveUserData(userDto: UserDto): Flow<Resource<Unit>>

    suspend fun updateAvatar(avatar: String): Flow<Resource<Unit>>

    suspend fun addFavourite(quizId: String): Flow<Resource<Unit>>

    suspend fun removeFavourite(quizId: String): Flow<Resource<Unit>>
}