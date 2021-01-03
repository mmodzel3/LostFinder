package com.github.mmodzel3.lostfinder.location

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.UserEndpoint
import com.github.mmodzel3.lostfinder.user.UserEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.user.UserEndpointFactory
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CurrentLocationService : Service() {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "Localisation"
        private const val NOTIFICATION_ID = 1;

        private const val LOCATION_ASK_INTERVAL = 10000
        private const val LOCATION_ASK_FASTEST_INTERVAL = 5000
        private const val LOCATION_ASK_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY

        private const val NOTIFICATION_ENDPOINT_ACCESS_ERROR_ID = 2002
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val binder = CurrentLocationBinder(this)
    private val listeners = ArrayList<CurrentLocationListener>()

    private var locationRequest : LocationRequest? = null
    private var locationCallback : LocationCallback? = null

    private var lastLocation : Location? = null

    private lateinit var userEndpoint: UserEndpoint
    private lateinit var endpointAccessErrorNotification: Notification
    private var endpointAccessErrorNotificationVisible = false
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = createServiceNotification()
        startForeground(NOTIFICATION_ID, notification)

        userEndpoint =
                UserEndpointFactory.createUserEndpoint(TokenManager.getInstance(applicationContext))
        endpointAccessErrorNotification = createEndpointAccessErrorNotification()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationListeningIfPossible()
    }

    override fun onDestroy() {
        super.onDestroy()

        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        hideEndpointAccessErrorNotificationIfVisible()
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
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getText(R.string.location_notification_title))
                .setContentText(getText(R.string.location_notification_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
    }

    private fun createEndpointAccessErrorNotification() : Notification {
        val title: CharSequence = getText(R.string.location_notification_title)
        val text: CharSequence = getText(R.string.location_err_endpoint_access)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_user_location_center)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setColor(Color.RED)
                .setAutoCancel(false)
                .setOngoing(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text).setBigContentTitle(title))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
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

    private fun showEndpointAccessErrorNotificationIfNotVisible() {
        if (!endpointAccessErrorNotificationVisible) {
            with(NotificationManagerCompat.from(this)) {
                notify(NOTIFICATION_ENDPOINT_ACCESS_ERROR_ID, endpointAccessErrorNotification)
            }

            endpointAccessErrorNotificationVisible = true
        }
    }

    private fun hideEndpointAccessErrorNotificationIfVisible() {
        if (endpointAccessErrorNotificationVisible) {
            with(NotificationManagerCompat.from(this)) {
                cancel(NOTIFICATION_ENDPOINT_ACCESS_ERROR_ID)
            }

            endpointAccessErrorNotificationVisible = false
        }
    }

    private fun startLocationListeningIfPossible() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            locationRequest = createLocationRequest()
            locationCallback = createLocationCallback()

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper())

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onLocationChange(location)
                }
            }
        }
    }

    private fun onLocationChange(location: Location) {
        lastLocation = location
        sendLocationChangeToAll(location)
        sendLocationChangeToServer(location)
    }

    private fun sendLocationChangeToServer(location: Location) {
        ioScope.launch {
            try {
                userEndpoint.updateUserLocation(Location(location.latitude, location.longitude))
                hideEndpointAccessErrorNotificationIfVisible()
            } catch (e: UserEndpointAccessErrorException) {
                showEndpointAccessErrorNotificationIfNotVisible()
            } catch (e: InvalidTokenException) {
                showEndpointAccessErrorNotificationIfNotVisible()
            }
        }
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
