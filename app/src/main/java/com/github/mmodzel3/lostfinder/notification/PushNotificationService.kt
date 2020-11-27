package com.github.mmodzel3.lostfinder.notification

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.UserEndpoint
import com.github.mmodzel3.lostfinder.user.UserEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.user.UserEndpointFactory
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class PushNotificationService : FirebaseMessagingService() {
    companion object {
        val last_notification = MutableLiveData<PushNotification>()
        const val NOTIFICATION_DEST_TOKEN_PREFERENCE_NAME = "notification_dest_token"
        const val NOTIFICATION_DEST_TOKEN_PREFERENCE_FIELD_NAME = "notification_dest_token"

        const val NOTIFICATION_DATA_TYPE_FIELD = "type"
        const val NOTIFICATION_DATA_FIELD = "data"

        fun getNotificationDestToken(context: Context): String? {
            val notificationDestTokenPreferences: SharedPreferences
                    = context.getSharedPreferences(NOTIFICATION_DEST_TOKEN_PREFERENCE_NAME, Context.MODE_PRIVATE)

            return notificationDestTokenPreferences.getString(NOTIFICATION_DEST_TOKEN_PREFERENCE_FIELD_NAME, null)
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    private lateinit var tokenManager: TokenManager
    private lateinit var userEndpoint: UserEndpoint
    private lateinit var notificationDestTokenPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        tokenManager = TokenManager.getInstance(applicationContext)
        userEndpoint = UserEndpointFactory.createUserEndpoint(tokenManager)
        notificationDestTokenPreferences = applicationContext.getSharedPreferences(NOTIFICATION_DEST_TOKEN_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    override fun onNewToken(token: String) {
        saveNotificationDestToken(token);
        sendNotificationDestTokenToServer(token);
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

    private fun saveNotificationDestToken(token: String) {
        with (notificationDestTokenPreferences.edit()) {
            putString(NOTIFICATION_DEST_TOKEN_PREFERENCE_FIELD_NAME, token)
            apply()
        }
    }

    private fun sendNotificationDestTokenToServer(token: String) {
        ioScope.launch {
            try {
                userEndpoint.updateUserNotificationDestToken(token)
            } catch (e: UserEndpointAccessErrorException) {
                Log.d("PushNotification", "Problem with User API access. Cannot send notification information to server.")
            } catch (e: InvalidTokenException) {
                Log.d("PushNotification", "Invalid user token. Cannot send notification information to server.")
            }
        }
    }
}