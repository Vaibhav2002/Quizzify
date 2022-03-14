package dev.vaibhav.quizzify.data.repo.quiz

import dev.vaibhav.quizzify.data.local.QuizLocalDataSource
import dev.vaibhav.quizzify.data.models.mapper.toQuiz
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.data.remote.quiz.InstantQuizDataSource
import dev.vaibhav.quizzify.data.remote.quiz.QuizRemoteDataSource
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.mapMessages
import dev.vaibhav.quizzify.util.mapTo
import dev.vaibhav.quizzify.util.mapToUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class QuizRepoImpl @Inject constructor(
    private val quizLocalDataSource: QuizLocalDataSource,
    private val quizRemoteDataSource: QuizRemoteDataSource,
    private val instantQuizDataSource: InstantQuizDataSource
) : QuizRepo {

    override val allCategories: Flow<List<CategoryDto>>
        get() = quizLocalDataSource.getAllCategories().flowOn(Dispatchers.IO)

    override suspend fun getAllQuizzes(
        query: String,
        categoryDto: CategoryDto?
    ): Flow<List<QuizDto>> =
        quizLocalDataSource.getAllQuizzes(query, categoryDto).flowOn(Dispatchers.IO)

    override suspend fun fetchAllCategories(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val resource = quizRemoteDataSource.getAllCategories()
        if (resource is Resource.Success)
            quizLocalDataSource.insertCategories(resource.data!!)
        emit(resource.mapToUnit())
    }.flowOn(Dispatchers.IO)

    override suspend fun fetchAllQuizzes(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val resource = quizRemoteDataSource.getAllQuizzes()
        if (resource is Resource.Success)
            quizLocalDataSource.insertQuizzes(resource.data!!.shuffled())
        emit(resource.mapToUnit())
    }.flowOn(Dispatchers.IO)

    override suspend fun fetchInstantQuiz(
        count: Int,
        category: CategoryDto
    ): Flow<Resource<QuizDto>> = flow {
        emit(Resource.Loading())
        val resource = instantQuizDataSource.getQuiz(count, category.id.toInt())
        emit(resource)
    }.map { quizResponse ->
        Timber.d(quizResponse.data.toString())
        quizResponse.mapTo { it.toQuiz(category) }
    }

    override suspend fun saveNewQuiz(quizDto: QuizDto): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val resource = quizRemoteDataSource.saveNewQuiz(quizDto)
        if (resource is Resource.Success)
            quizLocalDataSource.insertQuizzes(listOf(quizDto))
        emit(resource)
    }.map {
        it.mapMessages("Successfully saved quiz", "Failed to save quiz")
    }

    override suspend fun upvoteQuiz(quiz: QuizDto): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val newQuiz = quiz.copy(votes = quiz.votes + 1)
        val upvoteResource = quizRemoteDataSource.upvoteQuiz(newQuiz)
        if (upvoteResource is Resource.Success)
            quizLocalDataSource.insertQuizzes(listOf(newQuiz))
        emit(upvoteResource)
    }.map {
        it.mapMessages("Successfully up voted quiz", "Failed to upvote quiz")
    }

    override suspend fun getQuizzesCreatedByUser(userId: String) =
        getAllQuizzes("").map { quizzes ->
            quizzes.filter { it.createdByUserId == userId }
        }

    override suspend fun getCountOfQuizzesCreatedByUser(userId: String) =
        getQuizzesCreatedByUser(userId).map {
            it.count()
        }
}