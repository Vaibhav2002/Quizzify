package dev.vaibhav.quizzify.data.remote.quiz

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import dev.vaibhav.quizzify.GetAllCategoriesQuery
import dev.vaibhav.quizzify.GetAllQuizzesQuery
import dev.vaibhav.quizzify.SaveNewQuizMutation
import dev.vaibhav.quizzify.UpVoteQuizMutation
import dev.vaibhav.quizzify.data.local.room.QuizzifyTypeConverters
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.SafeHasura
import dev.vaibhav.quizzify.util.mapToUnit
import javax.inject.Inject

class HasuraQuizDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val safeHasura: SafeHasura
) : QuizRemoteDataSource {

    private val typeConverters = QuizzifyTypeConverters()

    override suspend fun getAllQuizzes(): Resource<List<QuizDto>> {
        val query = GetAllQuizzesQuery()
        return safeHasura.safeHasuraCall(
            call = { apolloClient.query(query).await() },
            result = {
                getQuizzesFromData(it)
            }
        )
    }

    override suspend fun getAllCategories(): Resource<List<CategoryDto>> {
        val query = GetAllCategoriesQuery()
        return safeHasura.safeHasuraCall(
            call = { apolloClient.query(query).await() },
            result = {
                getCategoryFromData(it)
            }
        )
    }

    override suspend fun saveNewQuiz(quizDto: QuizDto): Resource<Unit> {
        val mutation = SaveNewQuizMutation(
            id = Input.optional(quizDto.id),
            name = Input.optional(quizDto.name),
            categoryId = Input.optional(quizDto.category.id),
            createdBy = Input.optional(quizDto.createdBy),
            createdByUserId = Input.optional(quizDto.createdByUserId),
            description = Input.optional(quizDto.description),
            questions = Input.optional(typeConverters.fromQuestionList(quizDto.questions)),
            votes = Input.optional(quizDto.votes),
            timestamp = Input.optional(quizDto.timeStamp.toString())
        )
        return safeHasura.safeHasuraCall(
            call = { apolloClient.mutate(mutation).await() },
            result = { }
        ).mapToUnit()
    }

    override suspend fun upvoteQuiz(quizDto: QuizDto): Resource<Unit> {
        val mutation = UpVoteQuizMutation(quizDto.id, quizDto.votes)
        return safeHasura.safeHasuraCall(
            call = { apolloClient.mutate(mutation).await() },
            result = { }
        ).mapToUnit()
    }

    private suspend fun getQuizzesFromData(data: GetAllQuizzesQuery.Data) = data.quiz.map {
        val questions = typeConverters.toQuestionList(it.questions)
        QuizDto(
            name = it.name,
            description = it.description,
            createdBy = it.createdBy,
            createdByUserId = it.createdByUserId,
            votes = it.votes,
            category = CategoryDto(it.category!!.id, it.category.name, it.category.image),
            questions = questions,
            questionCount = questions.size,
            id = it.id,
            timeStamp = it.timestamp.toLong()
        )
    }

    private suspend fun getCategoryFromData(data: GetAllCategoriesQuery.Data) = data.category.map {
        CategoryDto(
            it.id,
            it.name,
            it.image
        )
    }
}