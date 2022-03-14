package dev.vaibhav.quizzify.ui.screens.auth.login

import dev.vaibhav.quizzify.util.ErrorType

sealed class LoginScreenEvents {
    data class ShowToast(val message: String) : LoginScreenEvents()
    object NavigateToMainScreen : LoginScreenEvents()
    object NavigateToRegisterScreen : LoginScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : LoginScreenEvents()
}
