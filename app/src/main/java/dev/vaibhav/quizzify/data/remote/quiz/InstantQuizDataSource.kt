package dev.vaibhav.quizzify.data.remote.quiz

import dev.vaibhav.quizzify.data.remote.api.ApiService
import dev.vaibhav.quizzify.util.safeApiCall
import javax.inject.Inject

class InstantQuizDataSource @Inject constructor(private val api: ApiService) {

    suspend fun getQuiz(count: Int, categoryId: Int) = safeApiCall {
        api.getQuiz(count, categoryId)
    }
}