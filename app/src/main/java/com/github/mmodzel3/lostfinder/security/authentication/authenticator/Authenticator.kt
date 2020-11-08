package com.github.mmodzel3.lostfinder.security.authentication.authenticator

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.github.mmodzel3.lostfinder.security.authentication.login.activity.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginInvalidCredentialsException
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginService
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginServiceBinder
import com.github.mmodzel3.lostfinder.security.encryption.Decryptor
import com.github.mmodzel3.lostfinder.security.encryption.DecryptorInterface
import kotlinx.coroutines.runBlocking


class Authenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

    lateinit var loginServiceBinder: LoginServiceBinder
    lateinit var loginServiceConnection: ServiceConnection

    init {
        bindToLoginService()
    }

    private fun bindToLoginService() {
        loginServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                loginServiceBinder = service as LoginServiceBinder
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }

        Intent(context, LoginService::class.java).also { intent ->
            context.bindService(intent, loginServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun editProperties(response: AccountAuthenticatorResponse,
                                accountType: String): Bundle {
        throw UnsupportedOperationException()
    }

    override fun addAccount(response: AccountAuthenticatorResponse,
                            accountType: String,
                            authTokenType: String,
                            requiredFeatures: Array<out String>,
                            options: Bundle): Bundle {

        return createLoginActivityIntentBundle(response)
    }

    private fun createLoginActivityIntentBundle(response: AccountAuthenticatorResponse) : Bundle {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)

        return bundle
    }

    override fun confirmCredentials(response: AccountAuthenticatorResponse?,
                                    account: Account?,
                                    options: Bundle?): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getAuthToken(response: AccountAuthenticatorResponse,
                              account: Account,
                              authTokenType: String,
                              options: Bundle): Bundle {

        val authToken = runBlocking {
            retrieveAccountAuthToken(account, authTokenType)
        }

        return if (authToken.isEmpty()) {
            createStoreTokenBundle(account, authToken)
        } else {
            createLoginActivityIntentBundle(response)
        }
    }

    private suspend fun retrieveAccountAuthToken(account: Account, authTokenType: String) : String {
        val accountManager: AccountManager = AccountManager.get(context)
        val authToken: String = accountManager.peekAuthToken(account, authTokenType)

        if (authToken.isNotEmpty()) {
            return authToken
        } else {
            try {
                val encryptedPassword: String = accountManager.getPassword(account)
                        ?: throw LoginInvalidCredentialsException()
                val decoder: DecryptorInterface = Decryptor.getInstance()
                val password: String = decoder.decrypt(encryptedPassword, context)

                return loginServiceBinder.login(account.name, password)
            } catch (e : LoginEndpointAccessErrorException) {
                return ""
            } catch (e : LoginInvalidCredentialsException) {
                return ""
            }
        }
    }

    private fun createStoreTokenBundle(account: Account, authToken: String) : Bundle {
        val result = Bundle()
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
        return result
    }

    override fun getAuthTokenLabel(authTokenType: String?): String {
        throw UnsupportedOperationException()
    }

    override fun updateCredentials(response: AccountAuthenticatorResponse?,
                                   account: Account?,
                                   authTokenType: String?,
                                   options: Bundle?): Bundle {
        throw UnsupportedOperationException()
    }

    override fun hasFeatures(response: AccountAuthenticatorResponse?,
                             account: Account?,
                             features: Array<out String>?): Bundle {
        throw UnsupportedOperationException()
    }
}