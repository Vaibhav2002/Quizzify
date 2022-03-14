package dev.vaibhav.quizzify.data.local.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import dev.vaibhav.quizzify.data.models.remote.UserDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataStoreImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    LocalDataStore {

    companion object {
        private val USER_KEY = stringPreferencesKey("UserData")
        private val LOGIN_KEy = booleanPreferencesKey("IsLoggedIn")
        private val ON_BOARDING_KEY = booleanPreferencesKey("onboarding")
    }

    override suspend fun isUserLoggedIn(): Boolean =
        dataStore.data.map { it[LOGIN_KEy] ?: false }.first()

    override suspend fun saveUserLoggedIn() {
        dataStore.edit { it[LOGIN_KEy] = true }
    }

    override suspend fun removeUserLoggedIn() {
        dataStore.edit { it.remove(LOGIN_KEy) }
    }

    override suspend fun getUserData(): UserDto =
        dataStore.data.map {
            Gson().fromJson(it[USER_KEY], UserDto::class.java)
        }.first()

    override suspend fun removeUserData() {
        dataStore.edit { it.remove(USER_KEY) }
    }

    override suspend fun saveUserData(userDto: UserDto) {
        dataStore.edit { it[USER_KEY] = Gson().toJson(userDto) }
    }

    override suspend fun isOnBoardingComplete(): Boolean {
        return dataStore.data.map { it[ON_BOARDING_KEY] ?: false }.first()
    }

    override suspend fun setOnBoardingComplete() {
        dataStore.edit { it[ON_BOARDING_KEY] = true }
    }
}