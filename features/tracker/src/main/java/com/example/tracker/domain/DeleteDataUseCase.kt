package com.example.tracker.domain

import javax.inject.Inject

class DeleteDataUseCase @Inject constructor(
    private val repository: TrackerRepository
) {

    suspend fun deleteData() {
       repository.deleteDataFromLocalBase()
    }
}