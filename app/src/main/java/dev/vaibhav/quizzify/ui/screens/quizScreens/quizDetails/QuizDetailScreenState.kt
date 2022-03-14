package dev.vaibhav.quizzify.ui.screens.quizScreens.quizDetails

data class QuizDetailScreenState(
    val title: String = "",
    val description: String = "",
    val image: String = "",
    val questionCount: String = "",
    val categoryName: String = "",
    val isLoading: Boolean = false
)
