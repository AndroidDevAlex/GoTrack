package com.example.intermediate

import com.example.database.LocationDao
import com.example.database.UserDao
import com.example.intermediate.data.ActionRepositoryImpl
import com.example.intermediate.domain.ActionRepository
import com.example.intermediate.domain.GetUserByIdUseCase
import com.example.intermediate.domain.LogOutUseCase
import com.example.networking.FirebaseAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ActionModule {

    @Provides
    @Singleton
    fun provideActionRepository(
        firebaseAuthManager: FirebaseAuthManager,
        userDao: UserDao,
        locationDao: LocationDao
    ): ActionRepository {
        return ActionRepositoryImpl(firebaseAuthManager, userDao, locationDao)
    }

    @Provides
    @Singleton
    fun provideLogOutUseCase(
        actionRepository: ActionRepository
    ): LogOutUseCase {
        return LogOutUseCase(actionRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserByIdUseCase(
        actionRepository: ActionRepository
    ): GetUserByIdUseCase {
        return GetUserByIdUseCase(actionRepository)
    }
}