package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.user.UserRole

enum class AlertType(val showNotificationAtStart: Boolean,
                     val showNotificationAtEnd: Boolean,
                     val createMinUserRolePermission: UserRole) {
    HELP(true, false, UserRole.USER),
    ANIMAL(true, false, UserRole.USER),
    FOUND_SOMETHING(true, false, UserRole.USER),
    FOUND_LOST(true, false, UserRole.USER),
    FOUND_WITNESS(true, false, UserRole.USER),
    LOST(true, false, UserRole.USER),
    SEARCH(true, true, UserRole.MANAGER),
    GATHER(true, true, UserRole.MANAGER)
}