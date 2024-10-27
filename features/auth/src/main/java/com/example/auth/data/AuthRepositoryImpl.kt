package com.example.auth.data

import com.example.auth.domain.AuthRepository
import com.example.data.UserData
import com.example.database.UserDao
import com.example.networking.FirebaseAuthManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuthManager: FirebaseAuthManager
): AuthRepository {
    override suspend fun createUserWithEmailAndPassword(email: String, password: String) {
        firebaseAuthManager.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuthManager.signInWithEmailAndPassword(email, password)
    }

    override fun getUserById(): Flow<UserData?> {
        return firebaseAuthManager.getUserUID()?.let {
            userDao.getUserById(it)
        } ?: flow { emit(null) }
    }

    override suspend fun saveUserToDB(email: String) {
        runCatching {
            val user = UserData(uid = firebaseAuthManager.getUserUID() ?: "", email = email)
            userDao.insertUser(user)
        }.onFailure { e ->
            e.printStackTrace()
        }
    }
}