package com.example.tracker.gpsStatus

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class TrackerStatusManagerImpl @Inject constructor(): TrackerStatusManager {

    private val isGpsEnabled = MutableStateFlow(false)

    override fun getGpsStatusFlow(): Flow<Boolean> = isGpsEnabled

    override fun setGpsStatus(isEnabled: Boolean) {
        isGpsEnabled.value = isEnabled
    }
}