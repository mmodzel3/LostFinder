package com.github.mmodzel3.lostfinder.authentication.token

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ServiceTestRule
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.authentication.login.LoginEndpointTestAbstract
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenAuthService
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenAuthServiceBinder
import com.github.mmodzel3.lostfinder.security.encryption.Encryptor
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TokenAuthServiceTest : LoginEndpointTestAbstract() {
    companion object {
        const val EMAIL_ADDRESS = "example@example.com"
        const val PASSWORD = "password"
        const val WRONG_PASSWORD = "wrong_password"
    }

    @get:Rule val serviceRule = ServiceTestRule()

    private lateinit var tokenAuthServiceBinder: TokenAuthServiceBinder
    private lateinit var accountManager: AccountManager
    private lateinit var accountType: String
    private lateinit var tokenType: String

    @Before
    override fun setUp() {
        super.setUp()

        accountManager = AccountManager.get(ApplicationProvider.getApplicationContext())
        accountType = ApplicationProvider.getApplicationContext<Context>()
                .resources.getString(R.string.account_type)
        tokenType = ApplicationProvider.getApplicationContext<Context>()
                .resources.getString(R.string.token_type)

        removeAccounts()
        tokenAuthServiceBinder = bindToService()
    }

    @After
    fun tearDown() {
        removeAccounts()
    }

    @Test
    fun whenNoAccountAndGetTokenThenInvalidTokenExceptionIsThrown() {
        runBlocking {
            assertThrows<InvalidTokenException> { tokenAuthServiceBinder.getToken() }
        }
    }

    @Test
    fun whenAccountWithWrongCredentialsAndGetTokenThenInvalidTokenExceptionIsThrown() {
        runBlocking {
            val encryptedPassword: String = encryptPassword(WRONG_PASSWORD)

            mockServerInvalidCredentialsResponse()
            accountManager.addAccountExplicitly(createTestAccount(), encryptedPassword, null)
            assertThrows<InvalidTokenException> { tokenAuthServiceBinder.getToken() }
        }
    }

    @Test
    fun whenAccountWithNoPasswordAndGetTokenThenInvalidTokenExceptionIsThrown() {
        runBlocking {
            mockServerInvalidCredentialsResponse()
            accountManager.addAccountExplicitly(createTestAccount(), null, null)
            assertThrows<InvalidTokenException> { tokenAuthServiceBinder.getToken() }
        }
    }

    @Test
    fun whenGetTokenAndServerReturnsErrorThenInvalidTokenExceptionIsThrown() {
        runBlocking {
            val encryptedPassword: String = encryptPassword(PASSWORD)

            mockServerFailureResponse()
            accountManager.addAccountExplicitly(createTestAccount(), encryptedPassword, null)
            assertThrows<InvalidTokenException> { tokenAuthServiceBinder.getToken() }
        }
    }

    @Test
    fun whenAccountWithCorrectCredentialsAndTokenIsNotCachedThenGotToken() {
        runBlocking {
            val encryptedPassword: String = encryptPassword(PASSWORD)

            mockServerTokenResponse()
            accountManager.addAccountExplicitly(createTestAccount(), encryptedPassword, null)
            val token: String = tokenAuthServiceBinder.getToken()
            assertThat(token).matches(TOKEN)
        }
    }

    @Test
    fun whenAccountWithCorrectCredentialsAndTokenIsCachedThenGotToken() {
        runBlocking {
            val encryptedPassword: String = encryptPassword(PASSWORD)
            val account: Account = createTestAccount()

            mockServerTokenResponse()
            accountManager.addAccountExplicitly(account, encryptedPassword, null)
            tokenAuthServiceBinder.getToken()

            mockServerFailureResponse()
            val token: String = tokenAuthServiceBinder.getToken()
            assertThat(token).matches(TOKEN)
        }
    }

    @Test
    fun whenAccountWithCachedTokenThenGotToken() {
        runBlocking {
            val encryptedPassword: String = encryptPassword(PASSWORD)
            val account: Account = createTestAccount()

            mockServerTokenResponse()
            accountManager.addAccountExplicitly(account, encryptedPassword, null)
            accountManager.setAuthToken(account, tokenType, TOKEN)

            val token: String = tokenAuthServiceBinder.getToken()
            assertThat(token).matches(TOKEN)
        }
    }

    private fun removeAccounts() {
        accountManager.getAccountsByType(accountType).forEach { accountManager.removeAccountExplicitly(it) }
    }

    private fun bindToService() : TokenAuthServiceBinder {
        val serviceIntent = Intent(ApplicationProvider.getApplicationContext<Context>(),
                TokenAuthService::class.java)

        val binder: IBinder = serviceRule.bindService(serviceIntent)

        return binder as TokenAuthServiceBinder
    }

    private fun createTestAccount() : Account {
        return Account(EMAIL_ADDRESS, accountType)
    }

    private fun encryptPassword(password: String) : String {
        return Encryptor.getInstance().encrypt(password, ApplicationProvider.getApplicationContext())
    }
}