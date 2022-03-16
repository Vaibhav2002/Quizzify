package dev.vaibhav.quizzify.ui.screens.auth.register

data class RegisterScreenState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,

    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val isRegisterButtonEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && username.isNotBlank() && !isLoading

    val areTextFieldsEnabled: Boolean
        get() = !isLoading
}