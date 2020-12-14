package com.github.mmodzel3.lostfinder.user

enum class UserRole {
    USER, MANAGER, OWNER;

    fun isManager(): Boolean {
        return this == MANAGER || this == OWNER
    }

    fun isOwner(): Boolean {
        return this == OWNER
    }
}