package com.github.mmodzel3.lostfinder.security.authentication.login

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.encryption.Encryptor
import com.github.mmodzel3.lostfinder.security.encryption.EncryptorInterface

abstract class LoginAccountManagerActivityAbstract : AppCompatActivity() {
    protected val account: Account
        get() = accountManager.getAccountsByType(accountType)[0]
    protected val isAccountPresent: Boolean
        get() = accountManager.getAccountsByType(accountType).isNotEmpty()
    private val accountManager: AccountManager by lazy { AccountManager.get(applicationContext) }
    private val accountType: String
        get() = applicationContext.resources.getString(R.string.account_type)
    private val tokenType: String
        get() = applicationContext.resources.getString(R.string.token_type)

    protected fun loginUsingAccountManager(emailAddress: String,
                                         password: String,
                                         accountManagerCallback: AccountManagerCallback<Bundle>) {
        removeAllAccounts()
        val account: Account = addAccount(emailAddress, password)

        accountManager.getAuthToken(account, tokenType, null, true, accountManagerCallback, null)
    }

    private fun removeAllAccounts() {
        accountManager.getAccountsByType(accountType).forEach { removeAccount(it) }
    }

    private fun removeAccount(account: Account) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(account, {}, null)
        } else {
            accountManager.removeAccountExplicitly(account)
        }
    }

    private fun addAccount(emailAddress: String, password: String) : Account {
        val encodedPassword: String = encryptPassword(password)
        val account = Account(emailAddress, accountType)
        accountManager.addAccountExplicitly(account, encodedPassword, null)

        return account
    }

    private fun encryptPassword(password: String) : String {
        val encryptor: EncryptorInterface = Encryptor.getInstance()
        return encryptor.encrypt(password, applicationContext)
    }

    fun removePasswordIfNeeded(removePassword: Boolean) {
        if (removePassword) {
            accountManager.setPassword(account, null)
        }
    }
}