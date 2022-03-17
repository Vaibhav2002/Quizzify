package dev.vaibhav.quizzify.ui.screens.main.favourites

import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.util.ErrorType

sealed class FavouritesScreenEvents {
    data class ShowToast(val message: String) : FavouritesScreenEvents()
    data class ShowErrorDialog(val errorType: ErrorType) : FavouritesScreenEvents()
    data class NavigateToQuizDetailScreen(val quizDto: QuizDto) : FavouritesScreenEvents()
}
