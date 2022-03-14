package dev.vaibhav.quizzify.util

import dev.vaibhav.quizzify.R

sealed class Resource<T>(
    open val data: T? = null,
    open val message: String = "",
    open val errorType: ErrorType = ErrorType.UNKNOWN
) {

    class Loading<T>() : Resource<T>()

    data class Success<T>(override val data: T?, override val message: String = "") :
        Resource<T>(data, message)

    data class Error<T>(
        override val errorType: ErrorType = ErrorType.UNKNOWN,
        override val message: String = errorType.errorMessage
    ) : Resource<T>(null, message, errorType)
}

enum class ErrorType(val title: String, val errorMessage: String, val lottieAnim: Int) {
    NO_INTERNET(
        "No Internet",
        "Looks like you don't have an active internet connection",
        R.raw.no_internet_anim
    ),
    UNKNOWN("Unknown Error", "Oops something went wrong", 0)
}
