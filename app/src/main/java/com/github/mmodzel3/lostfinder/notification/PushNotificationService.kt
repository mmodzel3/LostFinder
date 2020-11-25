package com.github.mmodzel3.lostfinder.notification

import android.app.Notification
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.UserEndpoint
import com.github.mmodzel3.lostfinder.user.UserEndpointFactory
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PushNotificationService : FirebaseMessagingService() {
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
        val notification: PushNotification = convertRemoveMessageToPushNotification(message)

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

    private fun convertRemoveMessageToPushNotification(message: RemoteMessage): PushNotification {
        val remoteNotification: RemoteMessage.Notification = message.notification!!
        val title: String = remoteNotification.title!!
        val body: String = remoteNotification.body!!
        val type: String = message.messageType!!
        val data: Map<String, String> = message.data

        return PushNotification(title, body, type, data)
    }
}