package com.example.tracker

import com.example.database.LocationDao
import com.example.database.UserDao
import com.example.network.NetworkStateObserver
import com.example.networking.FirebaseAuthManager
import com.example.networking.FirebaseFirestoreManager
import com.example.tracker.data.TrackerRepositoryImpl
import com.example.tracker.domain.DeleteDataUseCase
import com.example.tracker.domain.TrackerRepository
import com.example.tracker.domain.TrackerStatusUseCase
import com.example.tracker.gpsStatus.TrackerStatusManager
import com.example.tracker.gpsStatus.TrackerStatusManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackerModule {

    @Provides
    @Singleton
    fun provideTrackerStatusManager(): TrackerStatusManager {
        return TrackerStatusManagerImpl()
    }

    @Provides
    @Singleton
    fun provideTrackerStatusUseCase(
        trackerStatusManager: TrackerStatusManager,
        networkConnectivityObserver: NetworkStateObserver
    ): TrackerStatusUseCase {
        return TrackerStatusUseCase(trackerStatusManager, networkConnectivityObserver)
    }

    @Provides
    @Singleton
    fun provideDeleteDataUseCase(
        trackerRepository: TrackerRepository
    ): DeleteDataUseCase {
        return DeleteDataUseCase(trackerRepository)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        locationDao: LocationDao,
        userDao: UserDao,
        firebaseFirestore: FirebaseFirestoreManager,
        firebaseAuthManager: FirebaseAuthManager
    ): TrackerRepository {
        return TrackerRepositoryImpl(locationDao, userDao, firebaseFirestore, firebaseAuthManager)
    }
}