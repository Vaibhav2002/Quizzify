package dev.vaibhav.quizzify.data.models.mapper

import dev.vaibhav.quizzify.data.models.remote.*
import dev.vaibhav.quizzify.util.decodeHtml
import java.util.*

private fun getQuestion(
    question: String,
    incorrectAnswers: List<String>,
    correctAnswer: String
): QuestionDto {
    val correctAnswerIndex = Random().nextInt(4)
    val allOptions = incorrectAnswers
        .map { it.decodeHtml() }
        .shuffled()
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
        getQuestion(it.question.decodeHtml(), it.incorrectAnswers, it.correctAnswer)
    },
)