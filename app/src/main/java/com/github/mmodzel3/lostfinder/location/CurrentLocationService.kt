package com.github.mmodzel3.lostfinder.location

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.settings.SettingsActivity
import com.github.mmodzel3.lostfinder.user.UserEndpoint
import com.github.mmodzel3.lostfinder.user.UserEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.user.UserEndpointFactory
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.lang.Runnable


class CurrentLocationService : Service() {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "Localisation"
        private const val NOTIFICATION_ID = 1

        private const val LOCATION_ASK_INTERVAL = 30000L
        private const val LOCATION_ASK_FASTEST_INTERVAL = 15000L
        private const val LOCATION_ASK_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY

        private const val NOTIFICATION_ENDPOINT_ACCESS_ERROR_ID = 2002
        private const val NOTIFICATION_SHARING_LOCATION_UNAVAILABLE_ID = 2003

        private const val CHECK_LOCATION_SHARING_AVAILABILITY_INTERVAL = 5000L
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsSharedPreferences: SharedPreferences

    private val binder = CurrentLocationBinder(this)
    private val listeners = ArrayList<CurrentLocationListener>()

    private var locationRequest : LocationRequest? = null
    private var locationCallback : LocationCallback? = null

    private var lastLocation : Location? = null

    private lateinit var userEndpoint: UserEndpoint

    private lateinit var endpointAccessErrorNotification: Notification
    private lateinit var sharingLocationUnavailableNotification: Notification
    private var endpointAccessErrorNotificationVisible = false
    private var sharingLocationUnavailableNotificationVisible = false

    private var endpointAccessError = false

    private lateinit var handler: Handler
    private lateinit var sharingLocationRunnable: Runnable

    private val lock: Any = Any()
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
        sharingLocationUnavailableNotification = createLocationSharingUnavailableNotification()

        settingsSharedPreferences = getSharedPreferences(
            SettingsActivity.SETTINGS,
            Context.MODE_PRIVATE
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startPeriodicallyCheckingSharingLocationAvailability()
        startLocationListeningIfPossible()
    }

    override fun onDestroy() {
        super.onDestroy()

        stopLocationListening()
        stopPeriodicallyCheckingSharingLocationAvailability()

        hideEndpointAccessErrorNotificationIfVisible()
        hideSharingLocationUnavailableNotificationIfVisible()
    }

    override fun onBind(intent: Intent?): IBinder {
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

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_person_search)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setColor(Color.RED)
                .setAutoCancel(false)
                .setOngoing(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text).setBigContentTitle(title))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
    }

    private fun createLocationSharingUnavailableNotification() : Notification {
        val title: CharSequence = getText(R.string.location_notification_title)
        val text: CharSequence = getText(R.string.location_err_location_sharing_unavailable)

        val intent = Intent(this, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_person_search)
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

    private fun showSharingLocationUnavailableNotificationIfNotVisible() {
        if (!sharingLocationUnavailableNotificationVisible) {
            with(NotificationManagerCompat.from(this)) {
                notify(NOTIFICATION_SHARING_LOCATION_UNAVAILABLE_ID, sharingLocationUnavailableNotification)
            }

            sharingLocationUnavailableNotificationVisible = true
        }
    }

    private fun hideSharingLocationUnavailableNotificationIfVisible() {
        if (sharingLocationUnavailableNotificationVisible) {
            with(NotificationManagerCompat.from(this)) {
                cancel(NOTIFICATION_SHARING_LOCATION_UNAVAILABLE_ID)
            }

            sharingLocationUnavailableNotificationVisible = false
        }
    }

    private fun startLocationListeningIfPossible() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            locationRequest = createLocationRequest()
            locationCallback = createLocationCallback()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onLocationChange(location)
                }
            }
        }
    }

    private fun stopLocationListening() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    private fun onLocationChange(location: Location) {
        lastLocation = location
        sendLocationChangeToAll(location)

        if (isLocationSharingEnabled()) {
            sendLocationChangeToServer(location)
            onLocationSharingAvailable()
        } else {
            sendLocationChangeToServer(null)
            onLocationSharingUnavailable()
        }
    }

    private fun isLocationSharingEnabled(): Boolean {
        return settingsSharedPreferences.getBoolean(SettingsActivity.SHARE_LOCATION, true)
    }

    private fun startPeriodicallyCheckingSharingLocationAvailability() {
        handler = Handler(Looper.getMainLooper())
        sharingLocationRunnable = Runnable {
            checkSharingLocationAvailability()
            handler.postDelayed(sharingLocationRunnable, CHECK_LOCATION_SHARING_AVAILABILITY_INTERVAL)
        }

        handler.post(sharingLocationRunnable)
    }

    private fun checkSharingLocationAvailability() {
        val service = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (isGpsEnabled && isLocationSharingEnabled()) {
            onLocationSharingAvailable()
        } else {
            onLocationSharingUnavailable()
        }
    }

    private fun stopPeriodicallyCheckingSharingLocationAvailability() {
        handler.removeCallbacks(sharingLocationRunnable)
    }

    private fun onLocationSharingAvailable() = synchronized(lock) {
        hideSharingLocationUnavailableNotificationIfVisible()

        if (endpointAccessError) {
            showEndpointAccessErrorNotificationIfNotVisible()
        }
    }

    private fun onLocationSharingUnavailable() = synchronized(lock) {
        hideEndpointAccessErrorNotificationIfVisible()
        showSharingLocationUnavailableNotificationIfNotVisible()
    }

    private fun sendLocationChangeToServer(location: Location?) {
        ioScope.launch {
            blockingSendLocationToServer(location)
        }
    }

    private suspend fun blockingSendLocationToServer(location: Location?) {
        try {
            if (location != null) {
                userEndpoint.updateUserLocation(Location(location.latitude, location.longitude))
            } else {
                userEndpoint.clearUserLocation()
            }

            onEndpointAccessSuccess()
        } catch (e: UserEndpointAccessErrorException) {
            onEndpointAccessError()
        } catch (e: InvalidTokenException) {
            onEndpointAccessError()
        }
    }

    private fun onEndpointAccessSuccess() = synchronized(lock) {
        hideEndpointAccessErrorNotificationIfVisible()
        endpointAccessError = false
    }

    private fun onEndpointAccessError() = synchronized(lock) {
        if (isLocationSharingEnabled()) {
            showEndpointAccessErrorNotificationIfNotVisible()
        } else {
            showSharingLocationUnavailableNotificationIfNotVisible()
        }

        endpointAccessError = true
    }

    private fun createLocationRequest() : LocationRequest? {
        return LocationRequest.create()?.apply {
            interval = LOCATION_ASK_INTERVAL
            fastestInterval = LOCATION_ASK_FASTEST_INTERVAL
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
