package com.github.mmodzel3.lostfinder.notification

import android.app.Notification
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.UserEndpoint
import com.github.mmodzel3.lostfinder.user.UserEndpointFactory
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PushNotificationService : FirebaseMessagingService() {
    companion object {
        const val NOTIFICATION_DATA_TYPE_FIELD = "type"
        const val NOTIFICATION_DATA_FIELD = "data"
    }

    internal val notificationListeners: ArrayList<PushNotificationListener> = ArrayList()
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    private lateinit var tokenManager: TokenManager
    private lateinit var userEndpoint: UserEndpoint

    override fun onCreate() {
        super.onCreate()

        tokenManager = TokenManager.getInstance(applicationContext)
        userEndpoint = UserEndpointFactory.createUserEndpoint(tokenManager)
    }

    override fun onNewToken(token: String) {
        ioScope.launch {
            userEndpoint.updateUserNotificationDestToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val notification: PushNotification = convertRemoteMessageToPushNotification(message)

        notificationListeners.forEach {
            it.onNotificationReceive(notification)
        }
    }

    fun registerListener(listener: PushNotificationListener) {
        notificationListeners.add(listener)
    }

    fun unregisterListener(listener: PushNotificationListener) {
        notificationListeners.remove(listener)
    }

    private fun convertRemoteMessageToPushNotification(message: RemoteMessage): PushNotification {
        val remoteNotification: RemoteMessage.Notification = message.notification!!
        val title: String = remoteNotification.title!!
        val body: String = remoteNotification.body!!
        val type: String = message.data[NOTIFICATION_DATA_TYPE_FIELD]!!
        val jsonData: String = message.data[NOTIFICATION_DATA_FIELD]!!

        return PushNotification(title, body, type, jsonData)
    }
}