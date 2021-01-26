package com.github.mmodzel3.lostfinder.notification

import android.content.Context

class PushNotificationConverter private constructor() {
    companion object {
        private var pushNotificationConverter: PushNotificationConverter? = null

        fun getInstance(): PushNotificationConverter {
            if (pushNotificationConverter == null) {
                pushNotificationConverter = PushNotificationConverter()
            }

            return pushNotificationConverter!!
        }
    }

    private val pushNotificationChatMessageConverter = PushNotificationChatMessageConverter.getInstance()
    private val pushNotificationAlertConverter = PushNotificationAlertConverter.getInstance()

    fun convertAndNotify(context: Context, pushNotification: PushNotification) {
        if (pushNotification.type == PushNotificationChatMessageConverter.CHAT_MESSAGE_PUSH_NOTIFICATION_TYPE) {
            pushNotificationChatMessageConverter.convertAndNotify(context, pushNotification)
        } else if (pushNotification.type == PushNotificationAlertConverter.ALERT_PUSH_NOTIFICATION_TYPE) {
            pushNotificationAlertConverter.convertAndNotify(context, pushNotification)
        }
    }
}