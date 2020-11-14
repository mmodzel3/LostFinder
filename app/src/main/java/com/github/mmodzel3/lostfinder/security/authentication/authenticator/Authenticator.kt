package com.github.mmodzel3.lostfinder.security.authentication.authenticator

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.mmodzel3.lostfinder.security.authentication.login.*
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.encryption.Decryptor
import com.github.mmodzel3.lostfinder.security.encryption.DecryptorInterface
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.UnsupportedOperationException


class Authenticator(private val context: Context) : AbstractAccountAuthenticator(context) {
    companion object {
        const val AUTHENTICATOR_INFO = "AUTH_INFO"
        const val INVALID_CREDENTIALS = "Invalid credentials"
        const val LOGIN_ENDPOINT_ACCESS_ERROR = "Login endpoint access error"
        const val USER_DATA_SAVE_PASSWORD = "SAVE_PASSWORD"
    }

    private val accountManager: AccountManager = AccountManager.get(context)
    private val loginEndpoint: LoginEndpoint = LoginEndpointFactory.createLoginEndpoint()

    override fun editProperties(response: AccountAuthenticatorResponse?,
                                accountType: String?): Bundle {
        throw UnsupportedOperationException()
    }

    override fun addAccount(response: AccountAuthenticatorResponse?,
                            accountType: String?,
                            authTokenType: String?,
                            requiredFeatures: Array<out String>?,
                            options: Bundle?): Bundle {
        return createLoginActivityIntentBundle(response)
    }

    override fun confirmCredentials(response: AccountAuthenticatorResponse?,
                                    account: Account?,
                                    options: Bundle?): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getAuthToken(response: AccountAuthenticatorResponse,
                              account: Account,
                              authTokenType: String,
                              options: Bundle?): Bundle {
        return runBlocking {
            retrieveAccountAuthBundle(response, account, authTokenType)
        }
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

    private fun createLoginActivityIntentBundle(response: AccountAuthenticatorResponse?,
                                                info: String = "") : Bundle {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(AUTHENTICATOR_INFO, info)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)

        return bundle
    }

    internal suspend fun retrieveAccountAuthBundle(response: AccountAuthenticatorResponse,
                                                  account: Account,
                                                  authTokenType: String) : Bundle {
        return try {
            val token: String = retrieveAccountAuthToken(account, authTokenType)
            removeUserPasswordIfNeeded(account)
            accountManager.setAuthToken(account, authTokenType, token)
            createTokenBundle(account, token)
        } catch (e: LoginInvalidCredentialsException) {
            createAuthErrorBundle(response, INVALID_CREDENTIALS)
        } catch (e: LoginEndpointAccessErrorException) {
            createAuthErrorBundle(response, LOGIN_ENDPOINT_ACCESS_ERROR)
        }
    }

    internal suspend fun retrieveAccountAuthToken(account: Account, authTokenType: String) : String {
        val authToken: String? = accountManager.peekAuthToken(account, authTokenType)

        return if (!authToken.isNullOrEmpty()) {
            authToken
        } else {
            val password: String = getAccountEncodedPassword(account)
            retrieveAccountAuthTokenFromServer(account, password)
        }
    }

    private fun getAccountEncodedPassword(account: Account) : String {
        val encryptedPassword: String = accountManager.getPassword(account)
                ?: throw LoginInvalidCredentialsException()
        val decoder: DecryptorInterface = Decryptor.getInstance()

        return decoder.decrypt(encryptedPassword, context)
    }

    private suspend fun retrieveAccountAuthTokenFromServer(account: Account,
                                                           password: String) : String {

        val loginInfo: LoginInfo = loginEndpoint.login(account.name, password)

        if (loginInfo.token.isNotEmpty()) {
            return loginInfo.token
        } else {
            throw LoginInvalidCredentialsException()
        }
    }

    private fun createTokenBundle(account: Account, authToken: String) : Bundle {
        val result = Bundle()
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
        return result
    }

    private fun createAuthErrorBundle(response: AccountAuthenticatorResponse, error: String) : Bundle {
        return createLoginActivityIntentBundle(response, error)
    }

    private fun removeUserPasswordIfNeeded(account: Account) {
        val savePassword: Boolean? = accountManager.getUserData(account, USER_DATA_SAVE_PASSWORD)?.toBoolean()

        if (savePassword == null || !savePassword) {
            accountManager.clearPassword(account)
        }
    }
}