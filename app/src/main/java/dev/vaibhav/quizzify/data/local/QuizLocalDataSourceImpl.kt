package dev.vaibhav.quizzify.data.local

import dev.vaibhav.quizzify.data.local.room.QuizzifyDao
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizLocalDataSourceImpl @Inject constructor(private val dao: QuizzifyDao) :
    QuizLocalDataSource {
    override fun getAllQuizzes(query: String, category: CategoryDto?) = dao.getAllQuizzes()
        .map { quizzes ->
            val list = quizzes.filter {
                it.name.startsWith(query, ignoreCase = true)
            }
            category?.let { list.filter { it.category.id == category.id } } ?: list
        }

    override suspend fun insertQuizzes(quizzes: List<QuizDto>) = dao.insertQuizzes(quizzes)

    override fun getAllCategories() = dao.getAllCategories()

    override suspend fun insertCategories(categories: List<CategoryDto>) =
        dao.insertCategories(categories)
}