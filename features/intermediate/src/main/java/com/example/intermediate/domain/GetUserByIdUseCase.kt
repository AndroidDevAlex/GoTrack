package com.example.intermediate.domain

import com.example.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val actionRepository: ActionRepository
) {
    fun getUserById(): Flow<UserData?> {
        return actionRepository.getUserById()
    }
}