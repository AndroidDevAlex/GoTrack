package com.example.auth.domain

import com.example.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
     fun getUserById(): Flow<UserData?> {
        return authRepository.getUserById()
    }
}