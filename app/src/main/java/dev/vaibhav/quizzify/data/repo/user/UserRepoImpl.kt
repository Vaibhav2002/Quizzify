package dev.vaibhav.quizzify.data.repo.user

import dev.vaibhav.quizzify.data.local.dataStore.LocalDataStore
import dev.vaibhav.quizzify.data.models.remote.UserDto
import dev.vaibhav.quizzify.data.remote.user.UserDataSource
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.mapMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val localDataStore: LocalDataStore
) : UserRepo {

    override fun observeCurrentUser() = localDataStore.getUserDataFlow().flowOn(Dispatchers.IO)

    override suspend fun getCurrentUser() = localDataStore.getUserData()

    override suspend fun fetchUserData(userId: String): Flow<Resource<UserDto>> = flow {
        emit(Resource.Loading())
        emit(userDataSource.getUserData(userId))
    }.flowOn(Dispatchers.IO)

    override suspend fun saveUserData(userDto: UserDto): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val resource = userDataSource.saveUserData(userDto)
        if (resource is Resource.Success)
            localDataStore.saveUserData(userDto)
        emit(resource)
    }.flowOn(Dispatchers.IO)

    override suspend fun updateAvatar(avatar: String): Flow<Resource<Unit>> = flow {
        val newUser = getCurrentUser().copy(profilePic = avatar)
        emit(Resource.Loading())
        val resource = userDataSource.updateAvatar(newUser.userId, avatar)
        if (resource is Resource.Success) localDataStore.saveUserData(newUser)
        emit(resource)
    }.map { it.mapMessages("Updated Avatar", "Failed to update Avatar") }.flowOn(Dispatchers.IO)

    override suspend fun addFavourite(quizId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val user = getCurrentUser()
        val newFavourites = user.favourites.apply { toMutableList().add(quizId) }
        val newUser = user.copy(favourites = newFavourites)
        emit(updateFavourites(newUser))
    }.map { it.mapMessages("Added to favourites", "Failed to add to favourites") }
        .flowOn(Dispatchers.IO)

    override suspend fun removeFavourite(quizId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val user = getCurrentUser()
        val newFavourites = user.favourites.apply { toMutableList().remove(quizId) }
        val newUser = user.copy(favourites = newFavourites)
        emit(updateFavourites(newUser))
    }.map { it.mapMessages("Removed from favourites", "Failed to remove from favourites") }
        .flowOn(Dispatchers.IO)

    private suspend fun updateFavourites(user: UserDto): Resource<Unit> {
        val resource = userDataSource.updateUserFavourites(user.userId, user.favourites)
        if (resource is Resource.Success) localDataStore.saveUserData(user)
        return resource
    }
}