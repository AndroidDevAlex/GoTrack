package com.example.tracker.domain

import android.location.Location
import com.example.data.LocationData
import com.example.data.UserData

interface TrackerRepository {

    suspend fun saveLocationData(location: Location)

    suspend fun deleteLocationUser(locationData: LocationData)

    suspend fun getCoordinatesFromRoom(): List<LocationData>

    suspend fun getUsersFromRoom(): List<UserData>

    suspend fun sendAndDeleteCoordinate(coordinate: LocationData)

    suspend fun sendUsers(user: UserData)

    suspend fun deleteDataFromLocalBase()

    class LocationException(message: String) : Exception(message)
}