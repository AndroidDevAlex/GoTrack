package com.example.map.domain

import com.example.map.CoordinatesResult
import javax.inject.Inject

class GetCoordinatesUseCase @Inject constructor(
    private val mapRepository: MapRepository
) {

    suspend fun getCoordinatesForDay(selectedDate: Long): CoordinatesResult {
        val coordinates = mapRepository.getCoordinatesForDay(selectedDate)
        return when {
            coordinates.isEmpty() -> CoordinatesResult.NoData
            else -> CoordinatesResult.Success(coordinates.filterNotNull())
        }
    }
}