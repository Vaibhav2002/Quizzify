package dev.vaibhav.quizzify.ui.screens.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.local.dataStore.LocalDataStore
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SPLASH_TIME = 1500L

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepo: AuthRepository,
    private val localDataStore: LocalDataStore
) : ViewModel() {

    private val _events = MutableSharedFlow<SplashScreenEvents>()
    val events: SharedFlow<SplashScreenEvents> = _events

    private val isUserLoggedIn = authRepo.isUserLoggedIn

    private suspend fun isOnBoardingComplete() = localDataStore.isOnBoardingComplete()

    init {
        viewModelScope.launch {
            delay(SPLASH_TIME)
            onTimerComplete()
        }
    }

    private suspend fun onTimerComplete() {
        val event = when {
            !isOnBoardingComplete() -> SplashScreenEvents.NavigateToOnBoarding
            !isUserLoggedIn -> SplashScreenEvents.NavigateToLoginScreen
            else -> SplashScreenEvents.NavigateToHomeScreen
        }
        _events.emit(event)
    }
}
