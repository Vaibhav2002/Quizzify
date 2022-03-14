package dev.vaibhav.quizzify.ui.screens.auth.register

import dev.vaibhav.quizzify.util.ErrorType

sealed class RegisterScreenEvents {
    data class ShowToast(val message: String) : RegisterScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : RegisterScreenEvents()
    object NavigateToAvatarSelectScreen : RegisterScreenEvents()
    object NavigateToLoginScreen : RegisterScreenEvents()
    object NavigateToHomeScreen : RegisterScreenEvents()
}