package com.github.mmodzel3.lostfinder.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.server.ServerEndpointData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlin.random.Random

abstract class PushNotificationConverterAbstract<T: ServerEndpointData> {
    val lastNotification = MutableLiveData<T>()
    var showNotifications: Boolean = true

    private var isNotificationChannelCreated: Boolean = false

    abstract fun convertAndNotify(context: Context, pushNotification: PushNotification)
    protected abstract fun createNotificationChannel(context: Context)
    protected abstract fun createNotificationFromData(context: Context, data: T): Notification

    protected inline fun <reified S: ServerEndpointData> convertAndShowNotificationIfNecessary
            (context: Context, pushNotification: PushNotification) {
        val data: T = convertPushNotificationToData<S>(pushNotification)

        lastNotification.postValue(data)

        if (showNotifications) {
            val notification: Notification = createNotificationFromData(context, data)
            showNotification(context, notification)
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified S: ServerEndpointData>
            convertPushNotificationToData(pushNotification: PushNotification) : T {
        val gson: Gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create()

        val serverEndpointData: S = gson.fromJson(pushNotification.jsonData, S::class.java)
        return serverEndpointData as T
    }

    protected fun showNotification(context: Context, notification: Notification) {
        val notificationId: Int = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isNotificationChannelCreated) {
            createNotificationChannel(context)
            isNotificationChannelCreated = true
        }

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    protected fun createNotificationChannel(context: Context, notificationChannelId: String,
                                            channelName: String, channelDescription: String) {

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(notificationChannelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}