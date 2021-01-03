package com.github.mmodzel3.lostfinder.user

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.location.Location
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val userNameTextView: TextView = itemView.findViewById(R.id.activity_user_info_tv_username)
    private val emailTextView: TextView = itemView.findViewById(R.id.activity_user_info_tv_email)
    private val roleTextView: TextView = itemView.findViewById(R.id.activity_user_info_tv_role)
    private val lastLoginTextView: TextView = itemView.findViewById(R.id.activity_user_info_tv_last_login)
    private val increaseRoleButton: ImageButton
                    = itemView.findViewById(R.id.activity_user_info_bt_increase_permissions_role)
    private val decreaseRoleButton: ImageButton
                    = itemView.findViewById(R.id.activity_user_info_bt_decrease_permissions_role)
    private val blockAccountButton: ImageButton
            = itemView.findViewById(R.id.activity_user_info_bt_block_account)
    private val unblockAccountButton: ImageButton
            = itemView.findViewById(R.id.activity_user_info_bt_unblock_account)
    private val deleteAccountButton: ImageButton
            = itemView.findViewById(R.id.activity_user_info_bt_delete_account)

    var userName: CharSequence
        get() {
            return userNameTextView.text
        }

        set(value: CharSequence) {
            userNameTextView.text = value
        }

    var email: CharSequence
        get() {
            return emailTextView.text
        }

        set(value: CharSequence) {
            emailTextView.text = value
        }

    var role: UserRole
        get() {
            return UserRoleStringConverter.convertStringToRole(itemView.context, roleTextView.text.toString())
        }

        set(value: UserRole) {
            roleTextView.text = UserRoleStringConverter.convertRoleToString(itemView.context, value)
        }

    var lastLogin: Date?
        get() {
            return if (lastLoginTextView.text !=
                    itemView.context.getString(R.string.activity_user_info_last_login_no_information)) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                dateFormat.parse(lastLoginTextView.text.toString())!!
            } else {
                null
            }
        }

        set(value: Date?) {
            if (value != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                lastLoginTextView.text = dateFormat.format(value)
            } else {
                lastLoginTextView.text =
                    itemView.context.getString(R.string.activity_user_info_last_login_no_information)
            }
        }

    fun setIncreaseRoleClickListener(listener: () -> Unit) {
        increaseRoleButton.setOnClickListener {
            it.isEnabled = false
            listener()
            it.isEnabled = true
        }
    }

    fun hideIncreaseRoleButton() {
        increaseRoleButton.visibility = GONE
    }

    fun showIncreaseRoleButton() {
        increaseRoleButton.visibility = VISIBLE
    }

    fun setDecreaseRoleClickListener(listener: () -> Unit) {
        decreaseRoleButton.setOnClickListener {
            it.isEnabled = false
            listener()
            it.isEnabled = true
        }
    }

    fun hideDecreaseRoleButton() {
        decreaseRoleButton.visibility = GONE
    }

    fun showDecreaseRoleButton() {
        decreaseRoleButton.visibility = VISIBLE
    }

    fun setBlockAccountClickListener(listener: () -> Unit) {
        blockAccountButton.setOnClickListener {
            it.isEnabled = false
            listener()
            it.isEnabled = true
        }
    }

    fun hideBlockAccountButton() {
        blockAccountButton.visibility = GONE
    }

    fun showBlockAccountButton() {
        blockAccountButton.visibility = VISIBLE
    }

    fun setUnblockAccountClickListener(listener: () -> Unit) {
        unblockAccountButton.setOnClickListener {
            it.isEnabled = false
            listener()
            it.isEnabled = true
        }
    }

    fun hideUnblockAccountButton() {
        unblockAccountButton.visibility = GONE
    }

    fun showUnblockAccountButton() {
        unblockAccountButton.visibility = VISIBLE
    }

    fun setDeleteAccountClickListener(listener: () -> Unit) {
        deleteAccountButton.setOnClickListener {
            it.isEnabled = false
            listener()
            it.isEnabled = true
        }
    }

    fun hideDeleteAccountButton() {
        deleteAccountButton.visibility = GONE
    }

    fun showDeleteAccountButton() {
        deleteAccountButton.visibility = VISIBLE
    }
}