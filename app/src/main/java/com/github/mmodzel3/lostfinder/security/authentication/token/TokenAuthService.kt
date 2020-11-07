package com.github.mmodzel3.lostfinder.security.authentication.token

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.authenticator.Authenticator

class TokenAuthService : Service() {
    private val binder = TokenAuthServiceBinder(this)
    private val accountManager = AccountManager.get(applicationContext)

    private val accountType
        get() = applicationContext.resources.getString(R.string.account_type)

    private val account: Account?
        get() {
            val accounts: Array<out Account> = accountManager.getAccountsByType(accountType)

            return if (accounts.isNotEmpty()){
                accounts[0]
            } else {
                null
            }
        }

    val token: String
        get() = accountManager.blockingGetAuthToken(account, accountType, true)

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}