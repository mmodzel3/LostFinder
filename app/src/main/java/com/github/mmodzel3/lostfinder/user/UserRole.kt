package com.github.mmodzel3.lostfinder.user

enum class UserRole {
    USER, MANAGER, OWNER, NOT_LOGGED;

    fun isManager(): Boolean {
        return this == MANAGER || this == OWNER
    }

    fun isOwner(): Boolean {
        return this == OWNER
    }
}