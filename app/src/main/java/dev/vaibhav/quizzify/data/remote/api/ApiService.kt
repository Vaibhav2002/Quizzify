package dev.vaibhav.quizzify.data.remote.api

import dev.vaibhav.quizzify.data.models.remote.QuizResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api.php")
    suspend fun getQuiz(
        @Query("amount") count: Int,
        @Query("category") categoryId: Int,
        @Query("type") type: String = "multiple"
    ): Response<QuizResponse>
}