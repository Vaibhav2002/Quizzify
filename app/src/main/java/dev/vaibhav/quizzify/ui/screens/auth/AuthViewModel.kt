package dev.vaibhav.quizzify.ui.screens.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepo: AuthRepository) : ViewModel() {
    val isUserSignedIn = authRepo.isUserLoggedIn
}
