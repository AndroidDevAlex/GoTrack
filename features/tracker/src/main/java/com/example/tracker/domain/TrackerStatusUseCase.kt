package com.example.tracker.domain

import com.example.network.NetworkStateObserver
import com.example.tracker.TrackerState
import com.example.tracker.gpsStatus.TrackerStatusManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class TrackerStatusUseCase @Inject constructor(
    private val trackerStatusManager: TrackerStatusManager,
    private val networkConnectivityObserver: NetworkStateObserver
) {

    fun getTrackerStatus(): Flow<TrackerState>{
      return combine(
          trackerStatusManager.getGpsStatusFlow(),
          networkConnectivityObserver.isConnected
      ) { isTrackRunning, isInternetAvailable ->
          when{
              isTrackRunning && isInternetAvailable -> TrackerState.Enabled
              isTrackRunning && !isInternetAvailable -> TrackerState.NoInternet
              else -> TrackerState.Disabled
          }
      }
    }
}