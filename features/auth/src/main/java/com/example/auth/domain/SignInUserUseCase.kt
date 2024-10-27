package com.example.auth.domain

import javax.inject.Inject

class SignInUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        repository.signInWithEmailAndPassword(email, password)
    }
}