package com.github.mmodzel3.lostfinder.location

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.github.mmodzel3.lostfinder.R
import com.google.android.gms.location.*
import org.osmdroid.util.GeoPoint

class CurrentLocationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "Localisation"
    private val NOTIFICATION_ID = 1;

    private val LOCATION_ASK_INTERVAL = 10000
    private val LOCATION_ASK_FASTEST_INTERVAL = 5000
    private val LOCATION_ASK_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val binder = CurrentLocationBinder(this)
    private val listeners = ArrayList<CurrentLocationListener>()

    private var locationRequest : LocationRequest? = null
    private var locationCallback : LocationCallback? = null

    private var lastLocation : Location? = null

    override fun onCreate() {
        super.onCreate()

        val notification = createServiceNotification()
        startForeground(NOTIFICATION_ID, notification)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationListeningIfPossible()
    }

    override fun onDestroy() {
        super.onDestroy()

        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun registerListener(listener: CurrentLocationListener) {
        listeners.add(listener)

        lastLocation?.let { sendLocationChange(listener, it) }
    }

    fun unregisterListener(listener: CurrentLocationListener) {
        listeners.remove(listener)
    }

    private fun createServiceNotification() : Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getText(R.string.location_notification_title))
                .setContentText(getText(R.string.location_notification_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = getString(R.string.location_notification_channel_name)
        val descriptionText = getString(R.string.location_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun startLocationListeningIfPossible() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            locationRequest = createLocationRequest()
            locationCallback = createLocationCallback()

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                onLocationChange(location)
            }

            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }
    }

    private fun onLocationChange(location: Location) {
        lastLocation = location
        sendLocationChangeToAll(location)
    }

    private fun createLocationRequest() : LocationRequest? {
        return LocationRequest.create()?.apply {
            interval = LOCATION_ASK_INTERVAL.toLong()
            fastestInterval = LOCATION_ASK_FASTEST_INTERVAL.toLong()
            priority = LOCATION_ASK_PRIORITY
        }
    }

    private fun createLocationCallback() : LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.d("CurrentLocation", location.toString())
                    onLocationChange(location)
                }
            }
        }
    }

    private fun sendLocationChangeToAll(location: Location) {
        for (listener in listeners) {
            sendLocationChange(listener, location)
        }
    }

    private fun sendLocationChange(listener: CurrentLocationListener, location: Location) {
        listener.onLocalisationChange(location)
    }
}
