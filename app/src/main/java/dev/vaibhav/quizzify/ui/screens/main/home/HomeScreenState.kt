package dev.vaibhav.quizzify.ui.screens.main.home

import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto

data class HomeScreenState(
    val categories: List<CategoryDto> = emptyList(),
    val quizzes: List<QuizDto> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)
