package com.example.map.data

import com.example.data.LocationData
import com.example.database.LocationDao
import com.example.database.UserDao
import com.example.map.domain.MapRepository
import com.example.networking.FirebaseAuthManager
import com.example.networking.FirebaseFirestoreManager
import java.util.Calendar
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val locationDao: LocationDao,
    private val firebaseAuthManager: FirebaseAuthManager,
    private val firebaseFirestoreManager: FirebaseFirestoreManager
): MapRepository {

    override suspend fun getCoordinatesForDay(selectedDate: Long): List<LocationData?> {
        val uid = firebaseAuthManager.getUserUID()
        return uid?.let {
            runCatching {
                val startOfDayTimestamp = startOfDay(selectedDate)
                val endOfDayTimestamp = endOfDay(selectedDate)
                val documents = firebaseFirestoreManager
                    .getCoordinates(uid, startOfDayTimestamp, endOfDayTimestamp)

                if (documents.isEmpty()) {
                   throw MapRepository.MapException("No documents found")
                }

                return documents.map { document ->
                    document.toObject(LocationData::class.java)
                }.sortedBy { locationData -> locationData?.timeGettingCoordinate }
            }.getOrElse { e ->
                e.printStackTrace()
                emptyList()
            }
        } ?: emptyList()
    }

    private fun startOfDay(selectedDate: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun endOfDay(selectedDate: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    override suspend fun logOut() {
        firebaseAuthManager.signOut()
        userDao.deleteAllUsers()
        locationDao.deleteAllCoordinations()
    }
}