package com.example.map.domain

import com.example.data.LocationData

interface MapRepository {

    suspend fun getCoordinatesForDay(selectedDate: Long): List<LocationData?>
    suspend fun logOut()
    class MapException(message: String) : Exception(message)
}