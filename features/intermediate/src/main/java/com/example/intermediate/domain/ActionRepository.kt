package com.example.intermediate.domain

import com.example.data.UserData
import kotlinx.coroutines.flow.Flow

interface ActionRepository {

    suspend fun deleteDataFromLocalBase()

    fun getUserById(): Flow<UserData?>
}