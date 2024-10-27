package com.example.auth.domain

import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(email: String, password: String) {
        repository.createUserWithEmailAndPassword(email, password)
    }
}