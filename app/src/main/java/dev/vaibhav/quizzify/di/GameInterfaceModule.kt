package dev.vaibhav.quizzify.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.vaibhav.quizzify.data.remote.game.FirebaseGameDataSource
import dev.vaibhav.quizzify.data.remote.game.GameDataSource
import dev.vaibhav.quizzify.data.repo.game.GameRepo
import dev.vaibhav.quizzify.data.repo.game.GameRepoImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GameInterfaceModule {

    @Binds
    @Singleton
    abstract fun bindsGameDataSource(
        firebaseGameDataSource: FirebaseGameDataSource
    ): GameDataSource

    @Binds
    @Singleton
    abstract fun bindsGameRepo(
        gameRepoImpl: GameRepoImpl
    ): GameRepo
}