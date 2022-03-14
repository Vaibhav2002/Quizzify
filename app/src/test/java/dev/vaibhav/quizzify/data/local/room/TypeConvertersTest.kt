package dev.vaibhav.quizzify.data.local.room

import com.google.common.truth.Truth.assertThat
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.OptionDto
import dev.vaibhav.quizzify.data.models.remote.QuestionDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import org.junit.Before
import org.junit.Test

class TypeConvertersTest {

    private lateinit var quiz: QuizDto
    private lateinit var typeConverters: QuizzifyTypeConverters

    @Before
    fun setUp() {
        typeConverters = QuizzifyTypeConverters()
        quiz = QuizDto(
            category = CategoryDto(name = "Books"),
            questions = (0..6).map {
                QuestionDto(
                    question = "Hello $it",
                    options = (0..4).map { OptionDto("Option $it", false) }
                )
            }
        )
    }

    @Test
    fun `serializing from List of question to string`() {
        val stringVal = typeConverters.fromQuestionList(quiz.questions)

        // assert
        assertThat(stringVal).isNotEmpty()
    }

    @Test
    fun `serializing to List of question from string`() {
        val stringVal = typeConverters.fromQuestionList(quiz.questions)
        val questionList = typeConverters.toQuestionList(stringVal)
        println(questionList)
        // assert
        assertThat(questionList).isNotEmpty()
        assertThat(questionList[0]).isInstanceOf(QuestionDto::class.java)
        assertThat(questionList[0].options[0]).isInstanceOf(OptionDto::class.java)
    }
}