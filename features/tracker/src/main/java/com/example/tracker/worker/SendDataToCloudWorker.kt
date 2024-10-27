package com.example.tracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.network.NetworkStateObserver
import com.example.tracker.domain.TrackerRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class SendDataToCloudWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val locationRepository: TrackerRepository,
    private val networkStateObserver: NetworkStateObserver
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result{
        delay(1000)
        return runCatching {
            val isInternetAvailable = networkStateObserver.isConnected.first()

            if (isInternetAvailable) {
                val coordinatesRoom = locationRepository.getCoordinatesFromRoom()
                val usersRoom = locationRepository.getUsersFromRoom()

                if (coordinatesRoom.isNotEmpty()) {
                    locationRepository.sendAndDeleteCoordinate(coordinatesRoom.first())
                    Result.success()
                }

                if (usersRoom.isNotEmpty()) {
                    locationRepository.sendUsers(usersRoom.first())
                    Result.success()
                }
                Result.success()
            } else {
                Result.retry()
            }
        }.getOrElse { e ->
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        fun startWorker(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val syncWorkRequest = PeriodicWorkRequest.Builder(
                SendDataToCloudWorker::class.java,
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueue(syncWorkRequest)
        }
    }
}