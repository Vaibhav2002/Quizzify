package dev.vaibhav.quizzify.util

import com.google.firebase.FirebaseNetworkException
import timber.log.Timber

const val DATA_NULL = "Data is null"

object SafeFirebase {
    suspend fun <T> safeCall(
        handleNullCheck: Boolean = true,
        successMessage: String = "",
        errorMessage: String? = null,
        call: suspend () -> T?
    ): Resource<T> = try {
        val response = call()
        if (handleNullCheck) handleNullCheck(response, successMessage)
        else Resource.Success(data = response, message = successMessage)
    } catch (e: FirebaseNetworkException) {
        Timber.d(e.toString())
        Resource.Error(ErrorType.NO_INTERNET)
    } catch (e: Exception) {
        Timber.d(e.toString())
        Resource.Error(ErrorType.UNKNOWN, message = errorMessage ?: e.message.toString())
    }

    private fun <T> handleNullCheck(data: T?, successMessage: String): Resource<T> = data?.let {
        Resource.Success(data = it, successMessage)
    } ?: Resource.Error(message = DATA_NULL)
}