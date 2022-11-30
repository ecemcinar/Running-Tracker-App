package com.ecemsevvalcinar.runningtrackerapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ecemsevvalcinar.runningtrackerapp.R
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.ACTION_PAUSE_SERVICE
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.ACTION_STOP_SERVICE
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.FASTEST_LOCATION_INTERVAL
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.LOCATION_UPDATE_INTERVAL
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.NOTIFICATION_CHANNEL_ID
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.NOTIFICATION_ID
import com.ecemsevvalcinar.runningtrackerapp.other.TrackingUtility
import com.ecemsevvalcinar.runningtrackerapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    var isFirstRun = true

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        // LatLng --> map coordinates
        val pathPoints = MutableLiveData<Polylines>()
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    private fun addEmptyPolyLine() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location: Location?) {
        location?.let {
            val position = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(position)
                pathPoints.postValue(this)
            }
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Timber.d("New Location: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    @Suppress("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {

                val request = com.google.android.gms.location.LocationRequest.Builder(LOCATION_UPDATE_INTERVAL)
                    .setMinUpdateIntervalMillis(FASTEST_LOCATION_INTERVAL)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()

                fusedLocationClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper())
            }

        }
        else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service!")
                    pauseService()
                }
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service!")
                        startForegroundService()
                    }
                    Timber.d("Started or resumed service!")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service!")
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForegroundService() {
        addEmptyPolyLine()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle(getString(R.string.tracking_service_text_content_title))
            .setContentText(getString(R.string.statistics_text_counter_starter))
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

}