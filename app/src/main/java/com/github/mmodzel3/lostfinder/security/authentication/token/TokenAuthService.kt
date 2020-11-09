package com.github.mmodzel3.lostfinder.security.authentication.token

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.login.activity.LoginActivity

class TokenAuthService : Service() {
    private val binder = TokenAuthServiceBinder(this)
    private val accountManager by lazy { AccountManager.get(applicationContext) }

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

    override fun onBind(intent: Intent) : IBinder {
        return binder
    }

    suspend fun getToken(): String {
        if (account != null) {
            return getAndCheckTokenForAccount(account!!)
        } else {
            throw InvalidTokenException()
        }
    }

    private suspend fun getAndCheckTokenForAccount(account: Account) : String {
        val token: String = accountManager.blockingGetAuthToken(account, accountType, true)

        if (token.isNotEmpty()) {
            return token
        } else {
            throw InvalidTokenException()
        }
    }
}
