package com.github.mmodzel3.lostfinder.user

interface UserManagementListener {
    fun onIncreaseRoleClick(user: User)
    fun onDecreaseRoleClick(user: User)
    fun onBlockAccountClick(user: User)
    fun onUnblockAccountClick(user: User)
    fun onDeleteAccountClick(user: User)
}