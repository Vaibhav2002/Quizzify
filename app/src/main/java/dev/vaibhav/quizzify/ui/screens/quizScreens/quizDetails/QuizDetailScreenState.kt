package dev.vaibhav.quizzify.ui.screens.quizScreens.quizDetails

data class QuizDetailScreenState(
    val title: String = "",
    val description: String = "",
    val image: String = "",
    val questionCount: String = "",
    val categoryName: String = "",
    val isFavourite: Boolean = false,
    val isLoading: Boolean = false
) {
    val isFavButtonEnabled: Boolean
        get() = !isLoading

    val isGameButtonsEnabled: Boolean
        get() = !isLoading
}
