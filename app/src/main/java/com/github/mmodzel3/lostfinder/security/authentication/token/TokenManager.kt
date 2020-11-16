package com.github.mmodzel3.lostfinder.security.authentication.token

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AuthenticatorException
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.github.mmodzel3.lostfinder.R

class TokenManager private constructor(private val context: Context) {
    companion object {
        fun getInstance(context: Context): TokenManager {
            return TokenManager(context)
        }
    }

    private val accountManager by lazy { AccountManager.get(context) }

    private val accountType
        get() = context.resources.getString(R.string.account_type)

    private val account: Account?
        get() {
            val accounts: Array<out Account> = accountManager.getAccountsByType(accountType)

            return if (accounts.isNotEmpty()){
                accounts[0]
            } else {
                null
            }
        }

    suspend fun getToken(): String {
        if (account != null) {
            return getAndCheckTokenForAccount(account!!)
        } else {
            throw InvalidTokenException()
        }
    }

    private suspend fun getAndCheckTokenForAccount(account: Account) : String {
        val token: String = getTokenForAccount(account)

        if (token.isNotEmpty()) {
            return token
        } else {
            throw InvalidTokenException()
        }
    }

    private suspend fun getTokenForAccount(account: Account) : String {
        return try {
            accountManager.blockingGetAuthToken(account, accountType, true) ?: ""
        } catch (e: AuthenticatorException) {
            ""
        }
    }
}
