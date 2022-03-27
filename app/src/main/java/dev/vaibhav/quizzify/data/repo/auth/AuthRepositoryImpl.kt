package dev.vaibhav.quizzify.data.repo.auth

import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dev.vaibhav.quizzify.data.local.dataStore.LocalDataStore
import dev.vaibhav.quizzify.data.models.remote.UserDto
import dev.vaibhav.quizzify.data.remote.auth.AuthDataSource
import dev.vaibhav.quizzify.data.remote.user.UserDataSource
import dev.vaibhav.quizzify.util.*
import dev.vaibhav.quizzify.util.Constants.avatars
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource,
    private val dataStore: LocalDataStore,
    private val dispatchers: DispatcherProvider
) : AuthRepository {
    override val currentUserId: String
        get() = authDataSource.userId

    override val isUserLoggedIn: Boolean
        get() = authDataSource.isUserLoggedIn()

    override suspend fun getUserData(): UserDto = dataStore.getUserData()

    override suspend fun logoutUser() {
        authDataSource.logoutUser()
        dataStore.removeUserLoggedIn()
        dataStore.removeUserData()
    }

    override suspend fun loginUser(email: String, password: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val loginResource = authDataSource.loginUser(email, password)
        val resource = if (loginResource is Resource.Success) {
            val res = handleAfterLogin(loginResource.data!!)
            if (res is Resource.Success) loginResource else res
        } else loginResource
        emit(resource.mapToUnit())
    }.map {
        it.mapMessages("Successfully logged in user", errorMessage = "Failed to login User")
    }.flowOn(dispatchers.io)

    override suspend fun registerUser(
        username: String,
        email: String,
        password: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val registerResource = authDataSource.registerUser(email, username, password)
        val resource = if (registerResource is Resource.Success) {
            val res = handleAfterRegister(registerResource.data!!, username, email)
            if (res is Resource.Success) registerResource else res
        } else registerResource
        emit(resource.mapToUnit())
    }.flowOn(dispatchers.io)

    override suspend fun loginUsingGoogle(data: Intent): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val account = authDataSource.getGoogleAccount(data)
        if (account is Resource.Error) {
            emit(Resource.Error(message = "Failed to sign in using Google"))
            return@flow
        }
        val credential = GoogleAuthProvider.getCredential(account.data?.idToken, null)
        val signInResource = authDataSource.signInUsingCredential(credential)
        val resource = handleAfterGoogleLogin(signInResource)
        emit(resource)
    }.flowOn(dispatchers.io)

    private suspend fun handleAfterGoogleLogin(resource: Resource<FirebaseUser>): Resource<Unit> {
        return if (resource is Resource.Success) {
            val uid = resource.data!!.uid
            val username = resource.data!!.displayName ?: ""
            val email = resource.data!!.email ?: ""
            val profilePic = resource.data!!.photoUrl.toString()
            when (val userExistsResource = userDataSource.getUserData(uid)) {
                is Resource.Success -> {
                    saveUserDataInLocal(userExistsResource.data!!)
                    dataStore.saveUserLoggedIn()
                    resource.mapToUnit()
                }
                else -> {
                    if (userExistsResource.message == DATA_NULL)
                        handleAfterRegister(uid, username, email, profilePic)
                    else userExistsResource.mapToUnit()
                }
            }
        } else resource.mapToUnit()
    }

    private suspend fun handleAfterLogin(uid: String): Resource<Unit> {
        val saveResource = getUserDataAndSaveInLocal(uid)
        return if (saveResource is Resource.Error) {
            logoutUser()
            saveResource
        } else saveResource.mapToUnit()
    }

    private suspend fun handleAfterRegister(
        uid: String,
        username: String,
        email: String,
        profilePic: String = avatars.random()
    ): Resource<Unit> {
        val userDto = getUserDto(uid, username, email, profilePic)
        val saveResource = createNewUserDataAndSaveInLocal(userDto)
        return if (saveResource is Resource.Error) {
            removeUser()
            saveResource
        } else saveResource.mapToUnit()
    }

    private suspend fun getUserDataAndSaveInLocal(userId: String): Resource<Unit> {
        val userResource = userDataSource.getUserData(userId)
        if (userResource is Resource.Success) {
            saveUserDataInLocal(userResource.data!!)
            dataStore.saveUserLoggedIn()
        }
        return userResource.mapToUnit()
    }

    private suspend fun createNewUserDataAndSaveInLocal(userDto: UserDto): Resource<Unit> {
        val userResource = userDataSource.saveUserData(userDto)
        if (userResource is Resource.Success) {
            saveUserDataInLocal(userDto)
            dataStore.saveUserLoggedIn()
        }
        return userResource.mapToUnit()
    }

    private suspend fun removeUser() {
        authDataSource.removeUser()
        dataStore.removeUserData()
        dataStore.removeUserLoggedIn()
    }

    private suspend fun saveUserDataInLocal(userDto: UserDto) {
        dataStore.saveUserData(userDto)
    }

    private fun getUserDto(
        userId: String,
        username: String,
        email: String,
        profilePic: String
    ) = UserDto(userId, username, email, profilePic)
}