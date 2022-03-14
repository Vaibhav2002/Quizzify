package dev.vaibhav.quizzify.data.models.remote

import com.google.gson.annotations.SerializedName

data class QuizResponse(
    @SerializedName("response_code")
    val responseCode: Int = 0,
    @SerializedName("results")
    val results: List<Result> = listOf()
)