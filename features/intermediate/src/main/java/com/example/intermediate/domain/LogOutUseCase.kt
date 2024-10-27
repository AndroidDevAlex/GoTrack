package com.example.intermediate.domain

import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val repository: ActionRepository
) {

    suspend fun logOut() {
        repository.deleteDataFromLocalBase()
    }
}