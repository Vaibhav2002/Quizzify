package dev.vaibhav.quizzify.data.local.dataStore

import dev.vaibhav.quizzify.data.models.remote.UserDto
import kotlinx.coroutines.flow.Flow

interface LocalDataStore {

    suspend fun isUserLoggedIn(): Boolean

    suspend fun saveUserLoggedIn()

    suspend fun removeUserLoggedIn()

    suspend fun getUserData(): UserDto

    fun getUserDataFlow(): Flow<UserDto>

    suspend fun removeUserData()

    suspend fun saveUserData(userDto: UserDto)

    suspend fun isOnBoardingComplete(): Boolean

    suspend fun setOnBoardingComplete()
}