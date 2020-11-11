package com.github.mmodzel3.lostfinder.security.authentication.authenticator

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.lifecycleScope

class AuthenticatorService : Service() {
    private lateinit var authenticator: Authenticator

    override fun onCreate() {
        super.onCreate()

        authenticator = Authenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return authenticator.iBinder
    }
}