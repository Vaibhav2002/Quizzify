package dev.vaibhav.quizzify.ui.screens.splashScreen

sealed class SplashScreenEvents {
    object NavigateToOnBoarding : SplashScreenEvents()
    object NavigateToLoginScreen : SplashScreenEvents()
    object NavigateToHomeScreen : SplashScreenEvents()
}
