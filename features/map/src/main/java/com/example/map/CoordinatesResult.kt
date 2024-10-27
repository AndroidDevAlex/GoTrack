package com.example.map

import com.example.data.LocationData

sealed class CoordinatesResult {

    data class Success(val coordinates: List<LocationData>) : CoordinatesResult()
    data object NoData : CoordinatesResult()
    data object Loading : CoordinatesResult()
    data class Error(val message: String) : CoordinatesResult()
}