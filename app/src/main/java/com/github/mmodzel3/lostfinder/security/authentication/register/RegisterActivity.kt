package com.github.mmodzel3.lostfinder.security.authentication.register

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.server.ServerResponse
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val registerEndpoint: RegisterEndpoint by lazy {
        RegisterEndpointFactory.createRegisterEndpoint()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        initRegisterButton()
    }

    private fun initRegisterButton() {
        val registerButton: Button = findViewById(R.id.activity_register_bt_register)
        registerButton.setOnClickListener {
            onRegister()
        }

        enableRegisterButton()
    }

    private fun onRegister() {
        val emailEditText: EditText = findViewById(R.id.activity_register_et_email_address)
        val usernameEditText: EditText = findViewById(R.id.activity_register_et_username)
        val passwordEditText: EditText = findViewById(R.id.activity_register_et_password)
        val serverPasswordEditText: EditText = findViewById(R.id.activity_register_et_server_password)

        val email: String = emailEditText.text.toString()
        val username: String = usernameEditText.text.toString()
        val password: String = passwordEditText.text.toString()
        val serverPassword: String = serverPasswordEditText.text.toString()

        if (email.trim() != "" && username != "" && password != "") {
            disableRegisterButton()
            registerAccount(email, password, serverPassword, username)
        } else {
            Toast.makeText(this, R.string.activity_register_err_blank_fields, Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun registerAccount(emailAddress: String, password: String, serverPassword: String, username: String) {
        val activity: Activity = this
        lifecycleScope.launch {
            try {
                val response: ServerResponse = registerEndpoint.register(emailAddress, password, serverPassword, username)

                if (response == ServerResponse.OK) {
                    Toast.makeText(activity, R.string.activity_register_msg_success, Toast.LENGTH_SHORT)
                            .show()

                    finish()
                } else if (response == ServerResponse.DUPLICATED) {
                    Toast.makeText(activity, R.string.activity_register_err_duplicated, Toast.LENGTH_SHORT)
                            .show()
                    enableRegisterButton()
                } else if (response == ServerResponse.INVALID_PERMISSION) {
                    Toast.makeText(activity, R.string.activity_register_err_invalid_server_password, Toast.LENGTH_SHORT)
                        .show()
                    enableRegisterButton()
                } else if (response == ServerResponse.INVALID_PARAM) {
                    Toast.makeText(activity, R.string.activity_register_err_password_too_short, Toast.LENGTH_SHORT)
                        .show()
                    enableRegisterButton()
                }
            } catch (e: RegisterEndpointAccessErrorException) {
                Toast.makeText(activity, R.string.activity_register_err_api_access_problem, Toast.LENGTH_LONG)
                        .show()

                enableRegisterButton()
            }
        }
    }

    private fun disableRegisterButton() {
        val registerButton: Button = findViewById(R.id.activity_register_bt_register)
        registerButton.isEnabled = false
    }

    private fun enableRegisterButton() {
        val registerButton: Button = findViewById(R.id.activity_register_bt_register)
        registerButton.isEnabled = true
    }
}