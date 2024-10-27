package com.example.map

import com.example.database.LocationDao
import com.example.database.UserDao
import com.example.map.data.MapRepositoryImpl
import com.example.map.domain.GetCoordinatesUseCase
import com.example.map.domain.LogOutUseCase
import com.example.map.domain.MapRepository
import com.example.networking.FirebaseAuthManager
import com.example.networking.FirebaseFirestoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapModule {

    @Provides
    @Singleton
    fun provideMapRepository(
        locationDao: LocationDao,
        userDao: UserDao,
        firebaseAuthManager: FirebaseAuthManager,
        firebaseFirestoreManager: FirebaseFirestoreManager
    ): MapRepository {
        return MapRepositoryImpl(userDao, locationDao, firebaseAuthManager, firebaseFirestoreManager)
    }

    @Provides
    @Singleton
    fun provideLogOutUseCase(
        mapRepository: MapRepository
    ): LogOutUseCase {
        return LogOutUseCase(mapRepository)
    }

    @Provides
    @Singleton
    fun provideGetCoordinatesUseCase(
        mapRepository: MapRepository
    ): GetCoordinatesUseCase {
        return GetCoordinatesUseCase(mapRepository)
    }
}