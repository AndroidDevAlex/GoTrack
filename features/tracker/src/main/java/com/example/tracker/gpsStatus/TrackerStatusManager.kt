package com.example.tracker.gpsStatus

import kotlinx.coroutines.flow.Flow

interface TrackerStatusManager {

    fun getGpsStatusFlow(): Flow<Boolean>

    fun setGpsStatus(isEnabled: Boolean)
}