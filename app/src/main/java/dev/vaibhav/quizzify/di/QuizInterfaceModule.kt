package dev.vaibhav.quizzify.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.vaibhav.quizzify.data.local.QuizLocalDataSource
import dev.vaibhav.quizzify.data.local.QuizLocalDataSourceImpl
import dev.vaibhav.quizzify.data.remote.quiz.HasuraQuizDataSource
import dev.vaibhav.quizzify.data.remote.quiz.QuizRemoteDataSource
import dev.vaibhav.quizzify.data.repo.quiz.QuizRepo
import dev.vaibhav.quizzify.data.repo.quiz.QuizRepoImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class QuizInterfaceModule {

    @Binds
    @Singleton
    abstract fun bindsQuizLocalDataSource(
        quizLocalDataSourceImpl: QuizLocalDataSourceImpl
    ): QuizLocalDataSource

    @Binds
    @Singleton
    abstract fun bindsQuizRemoteDataSource(
        hasuraQuizDataSource: HasuraQuizDataSource
    ): QuizRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindsQuizRepo(
        quizRepoImpl: QuizRepoImpl
    ): QuizRepo
}