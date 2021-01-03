package com.github.mmodzel3.lostfinder.user

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager


class UserAdapter(private val tokenManager: TokenManager) : RecyclerView.Adapter<UserViewHolder>() {
    companion object {
        const val USER_TYPE = 1
    }

    var users: List<User> = ArrayList()
        set(value: List<User>) {
            field = value
        }

    private var userManagementListener: UserManagementListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_user_info, parent, false)

        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: User = users[position]

        holder.userName = user.username
        holder.email = user.email
        holder.role = user.role
        holder.lastLogin = user.lastLoginDate

        hideAllHolderManagementButtons(holder)

        if (tokenManager.getTokenRole() == UserRole.MANAGER) {
            prepareHolderManagementsButtonsForLoggedManager(holder, user)
        } else if (tokenManager.getTokenRole() == UserRole.OWNER) {
            prepareHolderManagementsButtonsForLoggedOwner(holder, user)
        }

        holder.setIncreaseRoleClickListener {
            onIncreaseRoleClick(user)
        }

        holder.setDecreaseRoleClickListener {
            onDecreaseRoleClick(user)
        }

        holder.setBlockAccountClickListener {
            onBlockAccountClick(user)
        }

        holder.setUnblockAccountClickListener {
            onUnblockAccountClick(user)
        }

        holder.setDeleteAccountClickListener {
            onDeleteAccountClick(user)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun getItemViewType(position: Int): Int {
        return USER_TYPE
    }

    fun setUserManagementListener(listener: UserManagementListener) {
        userManagementListener = listener
    }

    private fun hideAllHolderManagementButtons(holder: UserViewHolder) {
        holder.hideIncreaseRoleButton()
        holder.hideDecreaseRoleButton()
        holder.hideBlockAccountButton()
        holder.hideUnblockAccountButton()
        holder.hideDeleteAccountButton()
    }

    private fun prepareHolderManagementsButtonsForLoggedManager(holder: UserViewHolder, user: User) {
        if (user.role == UserRole.USER) {
            prepareHolderBlockButtons(holder, user)
            prepareHolderDeleteButton(holder)
        }
    }

    private fun prepareHolderManagementsButtonsForLoggedOwner(holder: UserViewHolder, user: User) {
        if (user.role != UserRole.OWNER) {
            prepareHolderPermissionsButtons(holder, user)
            prepareHolderBlockButtons(holder, user)
            prepareHolderDeleteButton(holder)
        }
    }

    private fun prepareHolderPermissionsButtons(holder: UserViewHolder, user: User) {
        if (user.role == UserRole.USER) {
            holder.showIncreaseRoleButton()
        } else {
            holder.showDecreaseRoleButton()
        }
    }

    private fun prepareHolderBlockButtons(holder: UserViewHolder, user: User) {
        if (!user.blocked) {
            holder.showBlockAccountButton()
        } else {
            holder.showUnblockAccountButton()
        }
    }

    private fun prepareHolderDeleteButton(holder: UserViewHolder) {
        holder.showDeleteAccountButton()
    }

    private fun onIncreaseRoleClick(user: User) {
        userManagementListener?.onIncreaseRoleClick(user)
    }

    private fun onDecreaseRoleClick(user: User) {
        userManagementListener?.onDecreaseRoleClick(user)
    }

    private fun onBlockAccountClick(user: User) {
        userManagementListener?.onBlockAccountClick(user)
    }

    private fun onUnblockAccountClick(user: User) {
        userManagementListener?.onUnblockAccountClick(user)
    }

    private fun onDeleteAccountClick(user: User) {
        userManagementListener?.onDeleteAccountClick(user)
    }
}