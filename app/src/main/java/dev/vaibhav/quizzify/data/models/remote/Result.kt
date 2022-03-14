package dev.vaibhav.quizzify.data.models.remote

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("correct_answer")
    val correctAnswer: String = "",
    @SerializedName("difficulty")
    val difficulty: String = "",
    @SerializedName("incorrect_answers")
    val incorrectAnswers: List<String> = listOf(),
    @SerializedName("question")
    val question: String = "",
    @SerializedName("type")
    val type: String = ""
)