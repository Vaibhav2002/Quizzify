package dev.vaibhav.quizzify.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizzifyDao {

    @Query("SELECT * FROM QUIZ_TABLE")
    fun getAllQuizzes(): Flow<List<QuizDto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizDto: List<QuizDto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryDto>)

    @Query("SELECT * FROM CATEGORY_TABLE")
    fun getAllCategories(): Flow<List<CategoryDto>>
}