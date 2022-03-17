package dev.vaibhav.quizzify.data.repo.auth

import android.content.Intent
import dev.vaibhav.quizzify.data.models.remote.UserDto
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUserId: String?

    val isUserLoggedIn: Boolean

    suspend fun getUserData(): UserDto

    suspend fun logoutUser()

    suspend fun loginUser(email: String, password: String): Flow<Resource<Unit>>

    suspend fun registerUser(
        username: String,
        email: String,
        password: String
    ): Flow<Resource<Unit>>

    suspend fun loginUsingGoogle(data: Intent): Flow<Resource<Unit>>
}