package dev.vaibhav.quizzify.ui.screens.auth.register

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegisterScreenEvents>()
    val events = _events.asSharedFlow()

    fun onUsernameChange(username: String) = viewModelScope.launch {
        _uiState.update { it.copy(username = username.trim()) }
    }

    fun onEmailTextChange(email: String) = viewModelScope.launch {
        _uiState.update { it.copy(email = email.trim()) }
    }

    fun onPasswordTextChange(password: String) = viewModelScope.launch {
        _uiState.update { it.copy(password = password.trim()) }
    }

    private fun verifyUserInput(): Boolean {
        val emailError = uiState.value.email.validateEmail()
        val passwordError = uiState.value.password.validatePassword()
        val usernameError = uiState.value.username.validateUsername()
        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                usernameError = usernameError
            )
        }
        return emailError == null && passwordError == null && usernameError == null
    }

    fun onRegisterButtonPressed() = viewModelScope.launch {
        if (verifyUserInput())
            registerUsingCredentials(
                uiState.value.username,
                uiState.value.email,
                uiState.value.password
            )
    }

    fun onGoogleRegisterPressed(data: Intent) = viewModelScope.launch {
        registerUsingGoogle(data)
    }

    fun onGoToLoginPress() = viewModelScope.launch {
        _events.emit(RegisterScreenEvents.NavigateToLoginScreen)
    }

    private suspend fun registerUsingCredentials(
        username: String,
        email: String,
        password: String
    ) {
        authRepo.registerUser(username, email, password).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(RegisterScreenEvents.NavigateToAvatarSelectScreen)
            }
        }
    }

    private suspend fun registerUsingGoogle(data: Intent) {
        authRepo.loginUsingGoogle(data).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(RegisterScreenEvents.NavigateToHomeScreen)
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) RegisterScreenEvents.ShowErrorDialog(error.errorType)
            else RegisterScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}
