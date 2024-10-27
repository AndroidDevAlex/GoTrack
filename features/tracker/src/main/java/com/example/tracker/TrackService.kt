package com.example.tracker

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.tracker.domain.TrackerRepository
import com.example.tracker.gpsStatus.TrackerStatusManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackService: Service() {

    companion object {
        const val START = "TRACK_ACTION_START"
        const val STOP = "TRACK_ACTION_STOP"
        const val CHANNEL_ID = "TRACKER_CHANNEL"
        const val TRACKER_NOTIFICATION_ID = 1
        const val CHANNEL_NAME = "My Location Channel"
        const val SENSITIVITY = 30.0F
        const val MIN_UPDATE_INTERVAL = 20000L
        const val LOCATION_UPDATE_INTERVAL = 30000L
    }

    private lateinit var locationCallback: LocationCallback

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var trackerStatusManager: TrackerStatusManager

    @Inject
    lateinit var locationRepository: TrackerRepository

    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START -> startService()
            STOP -> stopService()
        }
        return START_NOT_STICKY
    }

    private fun startService() {
        val notification: Notification = createNotification()
        startForeground(TRACKER_NOTIFICATION_ID, notification)
        trackerStatusManager.setGpsStatus(true)
    }

    private fun stopService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        trackerStatusManager.setGpsStatus(false)
        stopSelf()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.tracking_your_location))
            .setSmallIcon(R.drawable.location_searching)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return builder.build()
    }

    private fun setupLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = createRequest()
         locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation
                lastLocation?.let {
                    handleLocationUpdate(it)
                }
            }

        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    private fun createRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL).apply {
            setMinUpdateDistanceMeters(SENSITIVITY)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL)
        }.build()
    }

    private fun handleLocationUpdate(location: Location) {
        runCatching {
            job = CoroutineScope(Dispatchers.IO).launch {
                locationRepository.saveLocationData(location)
            }
        }.onFailure { e ->
            e.message
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
        job?.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}