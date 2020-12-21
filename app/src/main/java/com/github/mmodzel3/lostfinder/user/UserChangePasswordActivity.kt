package com.github.mmodzel3.lostfinder.user

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.LoggedUserActivityAbstract
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerResponse
import kotlinx.coroutines.launch

class UserChangePasswordActivity : LoggedUserActivityAbstract() {
    private lateinit var tokenManager: TokenManager
    private lateinit var userEndpoint: UserEndpoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user_change_password)

        tokenManager = TokenManager.getInstance(applicationContext)
        userEndpoint = UserEndpointFactory.createUserEndpoint(tokenManager)

        initChangePasswordButton()
    }

    private fun initChangePasswordButton() {
        val changePasswordButton: Button = findViewById(R.id.activity_user_change_password_bt_change_password)

        changePasswordButton.setOnClickListener {
            onChangePasswordClick()
        }

        enableChangePasswordButton()
    }

    private fun onChangePasswordClick() {
        val oldPasswordEditText: EditText = findViewById(R.id.activity_user_change_password_et_old_password)
        val newPasswordEditText: EditText = findViewById(R.id.activity_user_change_password_et_new_password)
        val repeatedNewPasswordEditText: EditText = findViewById(R.id.activity_user_change_password_et_repeated_new_password)

        val oldPassword: String = oldPasswordEditText.text.toString()
        val newPassword: String = newPasswordEditText.text.toString()
        val repeatedNewPassword: String = repeatedNewPasswordEditText.text.toString()

        if (newPassword == repeatedNewPassword) {
            changePassword(oldPassword, newPassword)
        } else {
            Toast.makeText(this, R.string.activity_user_change_password_err_not_same_new_passwords,
                Toast.LENGTH_LONG).show()
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        val activity: Activity = this

        disableChangePasswordButton()
        lifecycleScope.launch {
            try {
                val serverResponse: ServerResponse = userEndpoint.updateUserPassword(oldPassword, newPassword)

                if (serverResponse == ServerResponse.OK) {
                    Toast.makeText(activity, R.string.activity_user_change_password_msg_success,
                        Toast.LENGTH_SHORT).show()
                    finish()
                } else if (serverResponse == ServerResponse.INVALID_PARAM) {
                    Toast.makeText(activity, R.string.activity_user_change_password_err_invalid_old_password,
                        Toast.LENGTH_LONG).show()
                    enableChangePasswordButton()
                }

            } catch (e: InvalidTokenException) {
                Toast.makeText(activity, R.string.activity_user_change_password_err_invalid_token,
                    Toast.LENGTH_LONG).show()
                goToLoginActivity()
            } catch (e: UserEndpointAccessErrorException) {
                Toast.makeText(activity, R.string.activity_user_change_password_err_api_access_error,
                    Toast.LENGTH_LONG).show()
                enableChangePasswordButton()
            }
        }
    }

    private fun enableChangePasswordButton() {
        val changePasswordButton: Button = findViewById(R.id.activity_user_change_password_bt_change_password)
        changePasswordButton.isEnabled = true
    }

    private fun disableChangePasswordButton() {
        val changePasswordButton: Button = findViewById(R.id.activity_user_change_password_bt_change_password)
        changePasswordButton.isEnabled = false
    }
}