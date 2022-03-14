package dev.vaibhav.quizzify.data.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuestionDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.data.models.remote.game.Player
import java.lang.reflect.Type

class QuizzifyTypeConverters {

    @TypeConverter
    fun fromQuestionList(questions: List<QuestionDto>): String {
        val type: Type = object : TypeToken<List<QuestionDto?>?>() {}.type
        return Gson().toJson(questions, type)
    }

    @TypeConverter
    fun toQuestionList(questions: String): List<QuestionDto> {
        val type: Type = object : TypeToken<List<QuestionDto?>?>() {}.type
        return Gson().fromJson(questions, type)
    }

    @TypeConverter
    fun fromCategory(category: CategoryDto): String {
        return Gson().toJson(category)
    }

    @TypeConverter
    fun toCategory(category: String): CategoryDto {
        return Gson().fromJson(category, CategoryDto::class.java)
    }

    @TypeConverter
    fun fromPlayerList(players: List<Player>): String {
        val type: Type = object : TypeToken<List<Player?>?>() {}.type
        return Gson().toJson(players, type)
    }

    @TypeConverter
    fun toPlayerList(players: String): List<Player> {
        val type: Type = object : TypeToken<List<Player?>?>() {}.type
        return Gson().fromJson(players, type)
    }

    @TypeConverter
    fun fromQuiz(quiz: QuizDto): String {
        return Gson().toJson(quiz)
    }

    @TypeConverter
    fun toQuiz(quiz: String): QuizDto {
        return Gson().fromJson(quiz, QuizDto::class.java)
    }
}