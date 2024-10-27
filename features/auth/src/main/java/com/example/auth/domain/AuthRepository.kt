package com.example.auth.domain

import com.example.data.UserData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun createUserWithEmailAndPassword(email: String, password: String)

    suspend fun signInWithEmailAndPassword(email: String, password: String)

    fun getUserById(): Flow<UserData?>

    suspend fun saveUserToDB(email: String)
}