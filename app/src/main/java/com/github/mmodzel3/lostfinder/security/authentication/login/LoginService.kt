package com.github.mmodzel3.lostfinder.security.authentication.login

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.encryption.Encryptor
import com.github.mmodzel3.lostfinder.security.encryption.EncryptorInterface

class LoginService : LoginEndpointServiceAbstract() {
    private val binder = LoginServiceBinder(this)
    private val accountManager: AccountManager by lazy { AccountManager.get(applicationContext) }
    private val accountType
        get() = applicationContext.resources.getString(R.string.account_type)

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    suspend fun login(emailAddress: String, password: String): String {
        val token: String = sendLoginRequestAndGetToken(emailAddress, password)

        updateAccount(emailAddress, password)
        return token
    }

    private fun updateAccount(emailAddress: String, password: String?) {
        val accounts: Array<out Account> = accountManager.getAccountsByType(accountType)
        val encryptedPassword: String? = password?.let { encryptPassword(it) }

        if (accounts.isEmpty()){
            addAccount(emailAddress, encryptedPassword)
        } else {
            changeAccountCredentials(emailAddress, encryptedPassword)
        }
    }

    private fun encryptPassword(password: String) : String {
        val encryptor: EncryptorInterface = Encryptor.getInstance()
        return encryptor.encrypt(password, applicationContext)
    }

    private fun addAccount(emailAddress: String, encryptedPassword: String?) {
        val account = Account(emailAddress, accountType)
        accountManager.addAccountExplicitly(account, encryptedPassword, null)
    }

    private fun removeAccount() {
        val account: Account = accountManager.getAccountsByType(accountType).first()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(account, {}, null)
        } else {
            accountManager.removeAccountExplicitly(account)
        }
    }

    private fun changeAccountCredentials(emailAddress: String, encryptedPassword: String?) {
        val account: Account = accountManager.getAccountsByType(accountType).first()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            accountManager.setPassword(account, encryptedPassword)
            accountManager.renameAccount(account, emailAddress, null, null)
        } else {
            removeAccount()
            addAccount(emailAddress, encryptedPassword)
        }
    }
}