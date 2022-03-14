package dev.vaibhav.quizzify.ui.screens.main.profile

import dev.vaibhav.quizzify.util.ErrorType

sealed class ProfileScreenEvents {
    data class ShowToast(val message: String) : ProfileScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : ProfileScreenEvents()
    object NavigateToAuthScreen : ProfileScreenEvents()
    object NavigateToAboutScreen : ProfileScreenEvents()
}
