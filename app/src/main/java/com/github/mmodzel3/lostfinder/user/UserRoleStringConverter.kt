package com.github.mmodzel3.lostfinder.user

import android.content.Context
import com.github.mmodzel3.lostfinder.R

object UserRoleStringConverter {
    fun convertRoleToString(context: Context, role: UserRole) : String {
        return when (role) {
            UserRole.OWNER -> context.getString(R.string.role_owner)
            UserRole.MANAGER -> context.getString(R.string.role_manager)
            else -> context.getString(R.string.role_user)
        }
    }

    fun convertStringToRole(context: Context, roleString: String) : UserRole {
        return when (roleString) {
            context.getString(R.string.role_owner) -> UserRole.OWNER
            context.getString(R.string.role_manager) -> UserRole.MANAGER
            else -> UserRole.USER
        }
    }
}