package dev.vaibhav.quizzify.util

import android.text.TextUtils
import android.util.Patterns

fun String.validateEmail(): String? = when {
    isEmpty() -> "Email cannot be empty"
    !isValidEmail() -> "Invalid email"
    else -> null
}

fun String.validatePassword(): String? = when {
    isEmpty() -> "Email cannot be empty"
    !isPasswordValid() -> "Minimum password length is 6"
    else -> null
}

fun String.validateUsername() = when {
    isEmpty() -> "Username cannot be empty"
    else -> null
}

fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isPasswordValid() = this.isNotEmpty() && this.length >= 6