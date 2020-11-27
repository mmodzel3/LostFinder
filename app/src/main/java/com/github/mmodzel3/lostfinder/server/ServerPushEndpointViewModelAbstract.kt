package com.github.mmodzel3.lostfinder.server

import androidx.lifecycle.MediatorLiveData
import com.github.mmodzel3.lostfinder.notification.PushNotification
import com.github.mmodzel3.lostfinder.notification.PushNotificationService
import com.google.gson.Gson
import com.google.gson.GsonBuilder

abstract class ServerPushEndpointViewModelAbstract<T : ServerEndpointData>
    : ServerEndpointViewModelAbstract<T>() {

    override val data: MediatorLiveData<MutableMap<String, T>> = MediatorLiveData()

    internal inline fun <reified S: ServerEndpointData>
            listenToPushNotifications(pushNotificationType: String,
                                      crossinline onReceive: (S) -> Unit) {
        data.addSource(PushNotificationService.last_notification) {
            if (it.type == pushNotificationType) {
                val serverEndpointData: S = convertNotificationToServerEndpointData(it)
                onReceive(serverEndpointData)
            }
        }
    }

    private inline fun <reified S: ServerEndpointData>
            convertNotificationToServerEndpointData(notification: PushNotification) : S {
        val gson: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .create()

        return gson.fromJson(notification.jsonData, S::class.java)
    }
}