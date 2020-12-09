package com.github.mmodzel3.lostfinder.notification

data class PushNotification(val title: String,
                            val body: String,
                            val type: String,
                            val jsonData: String) {

}