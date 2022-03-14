package dev.vaibhav.quizzify.data.repo.quiz

import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.Flow

interface QuizRepo {

    val allCategories: Flow<List<CategoryDto>>

    suspend fun getAllQuizzes(
        query: String = "",
        categoryDto: CategoryDto? = null
    ): Flow<List<QuizDto>>

    suspend fun fetchAllCategories(): Flow<Resource<Unit>>

    suspend fun fetchAllQuizzes(): Flow<Resource<Unit>>

    suspend fun fetchInstantQuiz(count: Int, category: CategoryDto): Flow<Resource<QuizDto>>

    suspend fun saveNewQuiz(quizDto: QuizDto): Flow<Resource<Unit>>

    suspend fun upvoteQuiz(quiz: QuizDto): Flow<Resource<Unit>>

    suspend fun getQuizzesCreatedByUser(userId: String): Flow<List<QuizDto>>

    suspend fun getCountOfQuizzesCreatedByUser(userId: String): Flow<Int>
}