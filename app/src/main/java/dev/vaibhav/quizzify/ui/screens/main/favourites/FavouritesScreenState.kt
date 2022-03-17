package dev.vaibhav.quizzify.ui.screens.main.favourites

import dev.vaibhav.quizzify.data.models.remote.QuizDto

data class FavouritesScreenState(
    val quizzes: List<QuizDto> = emptyList(),
    val isLoading: Boolean = false
)
