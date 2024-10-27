package com.example.tracker.data

import android.location.Location
import com.example.data.LocationData
import com.example.data.UserData
import com.example.database.LocationDao
import com.example.database.UserDao
import com.example.networking.FirebaseAuthManager
import com.example.networking.FirebaseFirestoreManager
import com.example.tracker.domain.TrackerRepository
import javax.inject.Inject

class TrackerRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao,
    private val userDao: UserDao,
    private val firebaseFirestoreManager: FirebaseFirestoreManager,
    private val firebaseAuthManager: FirebaseAuthManager
): TrackerRepository{

    override suspend fun saveLocationData(location: Location) {
        runCatching {
            val userId = firebaseAuthManager.getUserUID()
            if (userId != null) {
                val timeGettingCoordinate = System.currentTimeMillis()
                val locationUser = LocationData(
                    0,
                    location.latitude,
                    location.longitude,
                    timeGettingCoordinate,
                    0
                )

                val newCoordinates = locationDao.insertLocation(locationUser)

                if (newCoordinates != -1L) {
                    firebaseFirestoreManager.sendLocationData(
                        userId,
                        location.latitude,
                        location.longitude,
                        timeGettingCoordinate,
                    )
                    deleteLocationUser(locationUser)
                } else {
                   throw TrackerRepository.LocationException("Database is empty")
                }
            } else {
                throw TrackerRepository.LocationException("Current user is missing")
            }
        }.onFailure { e ->
            throw TrackerRepository.LocationException("Error saving location data: $e")
        }
    }

    override suspend fun deleteLocationUser(locationData: LocationData) {
        runCatching {
            locationDao.deleteLocation(locationData)
        }.onFailure { e ->
            throw TrackerRepository.LocationException("Failed to delete data from Room: $e")
        }
    }

    override suspend fun getCoordinatesFromRoom(): List<LocationData> {
        return locationDao.getAllLocations()
    }

    override suspend fun getUsersFromRoom(): List<UserData> {
        return userDao.getAllUsers()
    }

    override suspend fun sendAndDeleteCoordinate(coordinate: LocationData) {
        val userId = firebaseAuthManager.getUserUID()
        if (userId != null) {
            firebaseFirestoreManager.sendLocationData(
                userId,
                coordinate.latitude,
                coordinate.longitude,
                coordinate.timeGettingCoordinate
            )

            deleteCoordinatesFromDataStore()
        }
    }

    override suspend fun sendUsers(user: UserData) {
        val userId = firebaseAuthManager.getUserUID()
        userId?.let { currentUserId ->
            firebaseFirestoreManager.sendUserData(currentUserId, user.email)
        }
    }

    override suspend fun deleteDataFromLocalBase() {
        firebaseAuthManager.signOut()
        userDao.deleteAllUsers()
        locationDao.deleteAllCoordinations()
    }

    private suspend fun deleteCoordinatesFromDataStore() {
        runCatching {
                locationDao.deleteAllCoordinations()
        }.onFailure { e ->
            throw TrackerRepository.LocationException("Coordinates successfully removed from Room database: $e")
        }
    }
}