package dev.vaibhav.quizzify.util

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafeHasura @Inject constructor(private val internetChecker: InternetChecker) {
    suspend fun <T, F> safeHasuraCall(
        call: suspend () -> Response<T>,
        result: suspend (T) -> F?,
    ): Resource<F> = withContext(Dispatchers.IO) {
        if (!internetChecker.hasInternetConnection())
            return@withContext Resource.Error(errorType = ErrorType.NO_INTERNET)
        return@withContext try {
            val response = call()
            if (response.hasErrors())
                response.getErrorResource<F>()
            else {
                response.data?.let { data: T ->
                    result(data)?.let { Resource.Success(it) }
                        ?: Resource.Error(message = DATA_NULL)
                } ?: Resource.Error(message = DATA_NULL)
            }
        } catch (e: ApolloException) {
            Timber.d(e.message)
            if (e is ApolloNetworkException || e is ApolloHttpException)
                Resource.Error(ErrorType.NO_INTERNET)
            else Resource.Error(message = e.message.toString())
        }
    }
}
