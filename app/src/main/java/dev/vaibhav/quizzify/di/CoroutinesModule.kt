package dev.vaibhav.quizzify.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.vaibhav.quizzify.util.AppDispatcherProvider
import dev.vaibhav.quizzify.util.DispatcherProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class CoroutinesModule {

    @Binds
    abstract fun bindsDispatcherProvider(
        appDispatcherProvider: AppDispatcherProvider
    ): DispatcherProvider
}