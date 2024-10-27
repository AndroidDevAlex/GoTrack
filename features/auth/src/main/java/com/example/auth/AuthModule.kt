package com.example.auth

import com.example.auth.data.AuthRepositoryImpl
import com.example.auth.domain.AuthRepository
import com.example.auth.domain.GetUserByIdUseCase
import com.example.auth.domain.RegisterUserUseCase
import com.example.auth.domain.SaveUserToDBUseCase
import com.example.auth.domain.SignInUserUseCase
import com.example.database.UserDao
import com.example.networking.FirebaseAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        firebaseAuthManager: FirebaseAuthManager
    ): AuthRepository {
        return AuthRepositoryImpl(userDao, firebaseAuthManager)
    }

    @Provides
    @Singleton
    fun provideRegisterUserUseCase(
        authRepository: AuthRepository
    ): RegisterUserUseCase {
        return RegisterUserUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideSaveUserToDBUseCase(
        authRepository: AuthRepository
    ): SaveUserToDBUseCase {
        return SaveUserToDBUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserByIdUseCase(
        authRepository: AuthRepository
    ): GetUserByIdUseCase {
        return GetUserByIdUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideSignInUserUseCase(
        authRepository: AuthRepository
    ): SignInUserUseCase {
        return SignInUserUseCase(authRepository)
    }
}