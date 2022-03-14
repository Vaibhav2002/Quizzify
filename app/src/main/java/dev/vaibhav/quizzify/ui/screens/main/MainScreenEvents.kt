package dev.vaibhav.quizzify.ui.screens.main

import dev.vaibhav.quizzify.util.ErrorType

sealed class MainScreenEvents {
    data class ShowToast(val message: String) : MainScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : MainScreenEvents()
}
