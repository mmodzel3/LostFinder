package com.github.mmodzel3.lostfinder.security.authentication.login.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginInvalidCredentialsException
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginService
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginServiceBinder
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    lateinit var loginServiceBinder: LoginServiceBinder
    lateinit var loginServiceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindToLoginService()
        setContentView(R.layout.activity_login)

        initLoginButton()
    }

    private fun initLoginButton() {
        val loginButton: Button = findViewById(R.id.activity_login_bt_login)
        loginButton.setOnClickListener { onLoginClick() }
    }

    private fun onLoginClick() {
        val emailAddress: EditText = findViewById(R.id.activity_login_et_email_address)
        val password: EditText = findViewById(R.id.activity_login_et_password)
        val savePassword: SwitchCompat = findViewById(R.id.activity_login_sw_save_password)

        login(emailAddress.text.toString(), password.text.toString(), savePassword.isChecked)
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

    private fun login(emailAddress: String, password: String, savePassword: Boolean) {
        val activity = this

        disableLogin()
        lifecycleScope.launch {
            try {
                loginServiceBinder.login(emailAddress, password, savePassword)
                goToMainActivity()
            } catch (e: LoginEndpointAccessErrorException) {
                Toast.makeText(activity, R.string.err_login_access, Toast.LENGTH_LONG).show()
                enableLogin()
            } catch (e: LoginInvalidCredentialsException) {
                Toast.makeText(activity, R.string.err_login_invalid_credentials, Toast.LENGTH_LONG).show()
                enableLogin()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
        finish()
    }
}