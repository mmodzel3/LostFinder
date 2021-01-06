package com.github.mmodzel3.lostfinder.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.chat.ChatActivity
import com.github.mmodzel3.lostfinder.chat.ChatMessage


class PushNotificationChatMessageConverter private constructor() : PushNotificationConverterAbstract<ChatMessage>() {
    companion object {
        const val CHAT_MESSAGE_PUSH_NOTIFICATION_TYPE = "chat"
        private const val NOTIFICATION_CHANNEL_ID = "Chat messages"

        private var pushNotificationConverter: PushNotificationChatMessageConverter? = null

        fun getInstance(): PushNotificationChatMessageConverter {
            if (pushNotificationConverter == null) {
                pushNotificationConverter = PushNotificationChatMessageConverter()
            }

            return pushNotificationConverter!!
        }
    }

    override fun convertAndNotify(context: Context, pushNotification: PushNotification) {
        convertAndShowNotificationIfNecessary<ChatMessage>(context, pushNotification)
    }

    override fun createNotificationFromData(context: Context, data: ChatMessage): Notification {
        val titlePrefix: String = context.getString(R.string.chat_message_notification_title_prefix)
        val title: String = titlePrefix + " " + data.user.username
        val text: String = data.msg

        val intent = Intent(context, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_person_search)
                .setContentTitle(title)
                .setContentText(text)
                .setVibrate(longArrayOf(500, 500))
                .setStyle(NotificationCompat.BigTextStyle().bigText(text).setBigContentTitle(title))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createNotificationChannel(context: Context) {
        val channelName = context.getString(R.string.chat_message_notification_channel_name)
        val channelDescription = context.getString(R.string.chat_message_notification_channel_description)

        createNotificationChannel(context, NOTIFICATION_CHANNEL_ID, channelName, channelDescription)
    }
}