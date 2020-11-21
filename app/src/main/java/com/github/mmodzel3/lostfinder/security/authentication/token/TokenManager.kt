package com.github.mmodzel3.lostfinder.security.authentication.token

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AuthenticatorException
import android.content.Context
import com.github.mmodzel3.lostfinder.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenManager private constructor(private val context: Context?) {
    companion object {
        var tokenManager: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            tokenManager = tokenManager ?: TokenManager(context)
            return tokenManager!!
        }
    }

    private val accountManager by lazy { AccountManager.get(context) }

    private val accountType
        get() = context?.resources?.getString(R.string.account_type)

    private val account: Account?
        get() {
            val accounts: Array<out Account> = accountManager.getAccountsByType(accountType)

            return if (accounts.isNotEmpty()){
                accounts[0]
            } else {
                null
            }
        }

    open fun getTokenEmailAddress() : String {
        if (account != null) {
            return account!!.name
        } else {
            throw InvalidTokenException()
        }
    }

    open suspend fun getToken(): String = withContext(Dispatchers.IO) {
        if (account != null) {
            return@withContext getAndCheckTokenForAccount(account!!)
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
