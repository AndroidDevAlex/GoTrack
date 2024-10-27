package com.example.networking

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @Named("MainDispatcher")
    fun provideMainDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }

    @Provides
    @Named("IODispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}