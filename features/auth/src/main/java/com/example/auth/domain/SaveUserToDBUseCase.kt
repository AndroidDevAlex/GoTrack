package com.example.auth.domain

import javax.inject.Inject

class SaveUserToDBUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String) {
        repository.saveUserToDB(email)
    }
}