package dev.vaibhav.quizzify.ui.screens.auth.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.util.ErrorType
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.validateEmail
import dev.vaibhav.quizzify.util.validatePassword
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginScreenEvents>()
    val events = _events.asSharedFlow()

    fun onEmailTextChange(email: String) = viewModelScope.launch {
        _uiState.update { it.copy(email = email.trim()) }
    }

    fun onPasswordTextChange(password: String) = viewModelScope.launch {
        _uiState.update { it.copy(password = password.trim()) }
    }

    private fun verifyUserInput(): Boolean {
        val emailError = uiState.value.email.validateEmail()
        val passwordError = uiState.value.password.validatePassword()
        _uiState.update {
            it.copy(emailError = emailError, passwordError = passwordError)
        }
        return emailError == null && passwordError == null
    }

    fun onLoginButtonPressed() = viewModelScope.launch {
        if (verifyUserInput())
            loginUsingCredentials(uiState.value.email, uiState.value.password)
    }

    fun onGoogleLoginPressed(data: Intent) = viewModelScope.launch {
        loginUsingGoogle(data)
    }

    fun onGoToRegisterPress() = viewModelScope.launch {
        _events.emit(LoginScreenEvents.NavigateToRegisterScreen)
    }

    private suspend fun loginUsingCredentials(email: String, password: String) {
        authRepo.loginUser(email, password).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(LoginScreenEvents.NavigateToMainScreen)
            }
        }
    }

    private suspend fun loginUsingGoogle(data: Intent) {
        authRepo.loginUsingGoogle(data).collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> handleError(it)
                is Resource.Loading -> Unit
                is Resource.Success -> _events.emit(LoginScreenEvents.NavigateToMainScreen)
            }
        }
    }

    private suspend fun handleError(error: Resource.Error<*>) {
        val event =
            if (error.errorType == ErrorType.NO_INTERNET) LoginScreenEvents.ShowErrorDialog(error.errorType)
            else LoginScreenEvents.ShowToast(error.message)
        _events.emit(event)
    }
}
