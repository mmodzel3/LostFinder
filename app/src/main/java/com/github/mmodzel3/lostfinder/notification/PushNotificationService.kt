package com.github.mmodzel3.lostfinder.notification

import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
    companion object {
        val last_notification = MutableLiveData<PushNotification>()

        const val NOTIFICATION_DATA_TYPE_FIELD = "type"
        const val NOTIFICATION_DATA_FIELD = "data"
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    private lateinit var localBroadcastManager: LocalBroadcastManager
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

        last_notification.postValue(notification)
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