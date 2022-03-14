package dev.vaibhav.quizzify.ui.screens.auth.avatarSelect

import dev.vaibhav.quizzify.util.ErrorType

sealed class AvatarSelectScreenEvents {
    object NavigateToHomeScreen : AvatarSelectScreenEvents()
    data class ShowToast(val message: String) : AvatarSelectScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : AvatarSelectScreenEvents()
}