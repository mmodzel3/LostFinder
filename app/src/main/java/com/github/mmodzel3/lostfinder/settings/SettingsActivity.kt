package com.github.mmodzel3.lostfinder.settings

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.LoggedUserActivityAbstract
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.UserChangePasswordActivity
import com.github.mmodzel3.lostfinder.user.UserEndpoint
import com.github.mmodzel3.lostfinder.user.UserEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.user.UserEndpointFactory
import kotlinx.coroutines.launch

class SettingsActivity : LoggedUserActivityAbstract() {
    private lateinit var tokenManager: TokenManager

    private val userEndpoint: UserEndpoint by lazy {
        UserEndpointFactory.createUserEndpoint(tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        tokenManager = TokenManager.getInstance(applicationContext)

        setUserInfo()
        initChangePasswordButton()
        initDeleteAccountButton()
    }

    private fun setUserInfo() {
        val usernameTextView: TextView = findViewById(R.id.activity_settings_tv_username)
        val emailTextView: TextView = findViewById(R.id.activity_settings_tv_email)

        usernameTextView.text = tokenManager.getTokenUsername()
        emailTextView.text = tokenManager.getTokenEmailAddress()
    }

    private fun initChangePasswordButton() {
        val changePasswordButton: ImageButton = findViewById(R.id.activity_settings_bt_change_password)

        changePasswordButton.setOnClickListener {
            goToUserChangePasswordActivity()
        }
    }

    private fun initDeleteAccountButton() {
        val deleteAccountButton: ImageButton = findViewById(R.id.activity_settings_bt_delete_account)

        deleteAccountButton.setOnClickListener {
            deleteAccount()
        }
    }

    private fun goToUserChangePasswordActivity() {
        val intent = Intent(this, UserChangePasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    private fun deleteAccount() {
        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        sendDeleteRequest()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }

        AlertDialog.Builder(this)
            .setMessage(R.string.activity_settings_msg_delete_account_are_you_sure)
            .setPositiveButton(R.string.yes, dialogClickListener)
            .setNegativeButton(R.string.no, dialogClickListener).show()
    }

    private fun sendDeleteRequest() {
        lifecycleScope.launch {
            try {
                userEndpoint.deleteUser()

                Toast.makeText(this@SettingsActivity, R.string.activity_settings_msg_delete_account_success,
                    Toast.LENGTH_LONG).show()

                goToLoginActivity()
            } catch (e: UserEndpointAccessErrorException) {
                Toast.makeText(this@SettingsActivity, R.string.activity_settings_err_delete_account_api_access_problem,
                    Toast.LENGTH_LONG).show()
            } catch (e: InvalidTokenException) {
                Toast.makeText(this@SettingsActivity, R.string.activity_settings_err_delete_account_invalid_token,
                    Toast.LENGTH_LONG).show()
                goToLoginActivity()
            }
        }
    }
}