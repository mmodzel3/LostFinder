package com.github.mmodzel3.lostfinder.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import kotlinx.coroutines.launch

class SplashScreenActivity: AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var splashScreenRunnable: Runnable

    private var canClose: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        delaySplashScreen()
        checkAccountStatus()
    }

    private fun checkAccountStatus() {
        lifecycleScope.launch {
            try {
                TokenManager.getInstance(applicationContext).getToken()
                goToMainActivity()
            } catch (e: InvalidTokenException) {
                goToLoginActivity()
            }
        }
    }

    private fun delaySplashScreen() {
        handler = Handler(Looper.getMainLooper())
        splashScreenRunnable = Runnable {
            setContentView(R.layout.activity_splash_screen)
        }

        handler.postDelayed(splashScreenRunnable, 100)
    }

    private fun goToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        handler.removeCallbacks(splashScreenRunnable)

        startActivity(mainIntent)
        finish()
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        handler.removeCallbacks(splashScreenRunnable)

        startActivity(intent)
        finish()
    }
}