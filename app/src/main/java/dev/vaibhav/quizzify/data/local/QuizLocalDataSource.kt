package dev.vaibhav.quizzify.data.local

import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import kotlinx.coroutines.flow.Flow

interface QuizLocalDataSource {

    fun getAllQuizzes(query: String, category: CategoryDto? = null): Flow<List<QuizDto>>

    suspend fun insertQuizzes(quizzes: List<QuizDto>)

    fun getAllCategories(): Flow<List<CategoryDto>>

    suspend fun insertCategories(categories: List<CategoryDto>)
}