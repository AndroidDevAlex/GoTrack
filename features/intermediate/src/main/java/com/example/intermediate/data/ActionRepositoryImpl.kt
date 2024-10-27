package com.example.intermediate.data

import com.example.data.UserData
import com.example.database.LocationDao
import com.example.database.UserDao
import com.example.intermediate.domain.ActionRepository
import com.example.networking.FirebaseAuthManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ActionRepositoryImpl @Inject constructor(
    private val firebaseAuthManager: FirebaseAuthManager,
    private val userDao: UserDao,
    private val locationDao: LocationDao
): ActionRepository {

    override suspend fun deleteDataFromLocalBase() {
        firebaseAuthManager.signOut()
        userDao.deleteAllUsers()
        locationDao.deleteAllCoordinations()
    }

    override fun getUserById(): Flow<UserData?> {
        return firebaseAuthManager.getUserUID()?.let {
            userDao.getUserById(it)
        } ?: flow { emit(null) }
    }
}