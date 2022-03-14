package dev.vaibhav.quizzify.data.remote.quiz

import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.util.Resource

interface QuizRemoteDataSource {

    suspend fun getAllQuizzes(): Resource<List<QuizDto>>

    suspend fun getAllCategories(): Resource<List<CategoryDto>>

    suspend fun saveNewQuiz(quizDto: QuizDto): Resource<Unit>

    suspend fun upvoteQuiz(quizDto: QuizDto): Resource<Unit>
}