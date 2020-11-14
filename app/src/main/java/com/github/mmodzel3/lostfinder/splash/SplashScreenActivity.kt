package com.github.mmodzel3.lostfinder.splash

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenAuthService
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenAuthServiceBinder
import kotlinx.coroutines.launch


class SplashScreenActivity: AppCompatActivity() {
    private lateinit var tokenAuthServiceBinder: TokenAuthServiceBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindToTokenAuthService()
        setContentView(R.layout.activity_splash_screen)
    }

    private fun bindToTokenAuthService() {
        val tokenAuthServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                tokenAuthServiceBinder = service as TokenAuthServiceBinder
                checkAccountStatus()
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }

        Intent(this, TokenAuthService::class.java).also { intent ->
            this.bindService(intent, tokenAuthServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun checkAccountStatus() {
        lifecycleScope.launch {
            try {
                tokenAuthServiceBinder.getToken()
                tokenAuthServiceBinder.getToken()
                goToMainActivity()
            } catch (e: InvalidTokenException) {
                goToLoginActivity()
            }
        }
    }

    private fun goToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(mainIntent)
        finish()
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
        finish()
    }
}