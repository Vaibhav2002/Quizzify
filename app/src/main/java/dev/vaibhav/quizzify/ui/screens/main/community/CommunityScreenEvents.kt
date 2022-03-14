package dev.vaibhav.quizzify.ui.screens.main.community

import dev.vaibhav.quizzify.data.models.remote.QuizDto

sealed class CommunityScreenEvents {
    data class NavigateToQuizDetailsScreen(val quiz: QuizDto) : CommunityScreenEvents()
    object NavigateToCreateQuizScreen : CommunityScreenEvents()
}