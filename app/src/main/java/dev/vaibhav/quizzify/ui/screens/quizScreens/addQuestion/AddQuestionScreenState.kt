package dev.vaibhav.quizzify.ui.screens.quizScreens.addQuestion

data class AddQuestionScreenState(
    val question: String = "",
    val options: List<String> = listOf("", "", "", ""),
    val answer: String = ""
) {
    val isAddButtonEnabled: Boolean
        get() = question.isNotEmpty() && options.none { it.isEmpty() } && options.contains(answer)
}
