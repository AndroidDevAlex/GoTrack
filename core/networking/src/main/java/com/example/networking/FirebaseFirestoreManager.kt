package com.example.networking

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class FirebaseFirestoreManager @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    @Named("IODispatcher") private val dispatcher: CoroutineDispatcher
) {

    suspend fun getCoordinates(
        uid: String, startOfDayTimestamp: Long, endOfDayTimestamp: Long
    ) = withContext(dispatcher) {

        return@withContext firebaseFirestore.collection(LOCATION_COLLECTION)
            .whereEqualTo(USER_ID_KEY, uid)
            .whereGreaterThanOrEqualTo(TIME_TO_SERVER_KEY, startOfDayTimestamp)
            .whereLessThanOrEqualTo(TIME_TO_SERVER_KEY, endOfDayTimestamp)
            .get()
            .await()
            .documents
    }

    suspend fun sendUserData(uidUser: String, email: String) = withContext(dispatcher) {
        val userReference: CollectionReference = firebaseFirestore.collection(USERS_COLLECTION)
        val documentReference: DocumentReference = userReference.document(uidUser)
        val newUserData = hashMapOf(
            USER_ID_KEY to uidUser,
            EMAIL_KEY to email
        )
        documentReference.set(newUserData, SetOptions.merge())
    }

    suspend fun sendLocationData(
        userId: String, latitude: Double, longitude: Double, timeGettingCoordinate: Long
    ) = withContext(dispatcher) {
        val timeToServer = System.currentTimeMillis()
        val locationReference: CollectionReference = firebaseFirestore.collection(LOCATION_COLLECTION)
        val newLocationData = hashMapOf(
            USER_ID_KEY to userId,
            LATITUDE_KEY to latitude,
            LONGITUDE_KEY to longitude,
            TIME_GETTING_COORDINATE_KEY to timeGettingCoordinate,
            TIME_TO_SERVER_KEY to timeToServer
        )
        locationReference.add(newLocationData)
    }

    companion object {
        const val USER_ID_KEY = "userId"
        const val EMAIL_KEY = "email"
        const val LATITUDE_KEY = "latitude"
        const val LONGITUDE_KEY = "longitude"
        const val TIME_GETTING_COORDINATE_KEY = "timeGettingCoordinate"
        const val TIME_TO_SERVER_KEY = "timeToServer"
        const val USERS_COLLECTION = "users"
        const val LOCATION_COLLECTION = "location"
    }
}