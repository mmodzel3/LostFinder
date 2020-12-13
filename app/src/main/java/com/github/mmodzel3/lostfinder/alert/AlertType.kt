package com.github.mmodzel3.lostfinder.alert

enum class AlertType(val showNotificationAtStart: Boolean, val showNotificationAtEnd: Boolean) {
    HELP(true, false),
    ANIMAL(true, false),
    FOUND_SOMETHING(true, false),
    FOUND_LOST(true, false),
    FOUND_WITNESS(true, false),
    LOST(true, false)
}