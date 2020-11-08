package com.github.mmodzel3.lostfinder.security.authentication.login.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginService
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginServiceBinder

class LoginActivity : AppCompatActivity() {
    lateinit var loginServiceBinder: LoginServiceBinder
    lateinit var loginServiceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindToLoginService()
        setContentView(R.layout.activity_login)
    }

    fun onLoginClick() {
        val emailAddress: EditText = findViewById(R.id.activity_login_et_email_address)
        val password: EditText = findViewById(R.id.activity_login_et_password)

        disableLogin()
        loginServiceBinder.login(emailAddress.text.toString(), password.text.toString())
        enableLogin()
    }

    private fun bindToLoginService() {
        loginServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                loginServiceBinder = service as LoginServiceBinder
                enableLogin()
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }

        Intent(applicationContext, LoginService::class.java).also { intent ->
            applicationContext.bindService(intent, loginServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun enableLogin() {
        val loginButton: Button = findViewById(R.id.activity_login_bt_login)
        loginButton.isEnabled = true
    }

    private fun disableLogin() {
        val loginButton: Button = findViewById(R.id.activity_login_bt_login)
        loginButton.isEnabled = false
    }
}