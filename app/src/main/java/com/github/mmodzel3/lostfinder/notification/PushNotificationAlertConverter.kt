package com.github.mmodzel3.lostfinder.notification

import android.app.Notification
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.alert.Alert
import com.github.mmodzel3.lostfinder.alert.AlertTypeTitleConverter

class PushNotificationAlertConverter private constructor() : PushNotificationConverterAbstract<Alert>() {
    companion object {
        const val ALERT_PUSH_NOTIFICATION_TYPE = "alert"
        private const val NOTIFICATION_CHANNEL_ID = "Alerts"

        private var pushNotificationConverter: PushNotificationAlertConverter? = null

        fun getInstance(): PushNotificationAlertConverter {
            if (pushNotificationConverter == null) {
                pushNotificationConverter = PushNotificationAlertConverter()
            }

            return pushNotificationConverter!!
        }
    }

    override fun convertAndNotify(context: Context, pushNotification: PushNotification) {
        val alert: Alert = convertPushNotificationToData<Alert>(pushNotification)

        lastNotification.postValue(alert)

        if (alert.endDate == null && alert.showNotificationAtStart) {
            showCorrectStartNotificationForAlert(context, alert)
        }

        if (alert.endDate != null && alert.showNotificationAtEnd) {
            showEndNotificationForAlert(context, alert)
        }
    }

    override fun createNotificationFromData(context: Context, data: Alert): Notification {
        val title: String = AlertTypeTitleConverter.convertAlertTypeToTitle(context, data.type)
        val from: String = context.getString(R.string.alert_notification_title_from)
        val fullTitle: String = title + " [" + from + " " + data.user.username + "]"
        val text: String = data.description

        return createAlertNotification(context, fullTitle, text)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createNotificationChannel(context: Context) {
        val channelName = context.getString(R.string.alert_notification_channel_name)
        val channelDescription = context.getString(R.string.alert_notification_channel_description)

        createNotificationChannel(context, NOTIFICATION_CHANNEL_ID, channelName, channelDescription)
    }

    private fun createAlertNotification(context: Context, title: String, text: String): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_user_location_center)
                .setColor(Color.RED)
                .setContentTitle(title)
                .setContentText(text)
                .setVibrate(longArrayOf(1000, 1000))
                .setStyle(NotificationCompat.BigTextStyle().bigText(text).setBigContentTitle(title))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
    }

    private fun showStartNotificationForAlert(context: Context, alert: Alert) {
        val prefixTitle: String = context.getString(R.string.alert_notification_start_alert_title_prefix)
        val title: String = prefixTitle + ' ' + AlertTypeTitleConverter.convertAlertTypeToTitle(context, alert.type)
        val text: String = alert.description

        val notification: Notification = createAlertNotification(context, title, text)
        showNotification(context, notification)
    }

    private fun showEndNotificationForAlert(context: Context, alert: Alert) {
        val prefixTitle: String = context.getString(R.string.alert_notification_end_alert_title_prefix)
        val title: String = prefixTitle + ' ' +
                AlertTypeTitleConverter.convertAlertTypeToTitle(context, alert.type)
        val text: String = alert.description

        val notification: Notification = createAlertNotification(context, title, text)
        showNotification(context, notification)
    }

    private fun showNotificationWithoutPrefixForAlert(context: Context, alert: Alert) {
        val notification: Notification = createNotificationFromData(context, alert)
        showNotification(context, notification)
    }

    private fun showCorrectStartNotificationForAlert(context: Context, alert: Alert) {
        if (alert.showNotificationAtEnd) {
            showStartNotificationForAlert(context, alert)
        } else {
            showNotificationWithoutPrefixForAlert(context, alert)
        }
    }
}