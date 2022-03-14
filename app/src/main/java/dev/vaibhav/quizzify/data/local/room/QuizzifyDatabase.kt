package dev.vaibhav.quizzify.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto

@Database(entities = [QuizDto::class, CategoryDto::class], version = 1, exportSchema = false)
@TypeConverters(QuizzifyTypeConverters::class)
abstract class QuizzifyDatabase : RoomDatabase() {

    abstract fun getDao(): QuizzifyDao
}