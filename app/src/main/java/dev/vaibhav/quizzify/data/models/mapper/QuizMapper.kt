package dev.vaibhav.quizzify.data.models.mapper

import dev.vaibhav.quizzify.data.models.remote.*
import java.util.*

private fun getQuestion(
    question: String,
    incorrectAnswers: List<String>,
    correctAnswer: String
): QuestionDto {
    val correctAnswerIndex = Random().nextInt(4)
    val allOptions = incorrectAnswers.shuffled()
        .toMutableList()
        .apply {
            add(correctAnswerIndex, correctAnswer)
        }
        .mapIndexed { index, s ->
            OptionDto(s, index == correctAnswerIndex)
        }
    return QuestionDto(
        question = question,
        options = allOptions
    )
}

fun QuizResponse.toQuiz(categoryDto: CategoryDto) = QuizDto(
    id = UUID.randomUUID().toString(),
    name = categoryDto.name,
    category = categoryDto,
    questions = results.map {
        getQuestion(it.question, it.incorrectAnswers, it.correctAnswer)
    },
)