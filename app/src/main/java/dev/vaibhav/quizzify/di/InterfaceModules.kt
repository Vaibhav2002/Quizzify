package dev.vaibhav.quizzify.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.vaibhav.quizzify.data.local.dataStore.LocalDataStore
import dev.vaibhav.quizzify.data.local.dataStore.LocalDataStoreImpl
import dev.vaibhav.quizzify.data.remote.auth.AuthDataSource
import dev.vaibhav.quizzify.data.remote.auth.FirebaseAuthDataSource
import dev.vaibhav.quizzify.data.remote.user.HasuraUserDataSource
import dev.vaibhav.quizzify.data.remote.user.UserDataSource
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.data.repo.auth.AuthRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class InterfaceModules {

    @Binds
    abstract fun bindsLocalDataStore(
        localDataStoreImpl: LocalDataStoreImpl
    ): LocalDataStore

    @Binds
    abstract fun bindsAuthDataSource(
        firebaseAuthDataSource: FirebaseAuthDataSource
    ): AuthDataSource

    @Binds
    abstract fun bindsUserDataSource(
        hasuraUserDataSource: HasuraUserDataSource
    ): UserDataSource

    @Binds
    abstract fun bindsAuthRepo(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}