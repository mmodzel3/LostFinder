package com.github.mmodzel3.lostfinder.security.authentication.token

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.authenticator.Authenticator
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointTestAbstract
import com.github.mmodzel3.lostfinder.security.encryption.Encryptor
import com.github.mmodzel3.lostfinder.server.ServerResponse
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class TokenManagerTest : LoginEndpointTestAbstract() {
    companion object {
        const val EMAIL_ADDRESS = "example@example.com"
        const val PASSWORD = "password"
        const val WRONG_PASSWORD = "wrong_password"
    }

    private var tokenManager = TokenManager.getInstance(ApplicationProvider.getApplicationContext())
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
    }

    @After
    override fun tearDown() {
        super.tearDown()

        removeAccounts()
    }

    @Test
    fun whenNoAccountAndGetTokenThenInvalidTokenExceptionIsThrown() {
        runBlocking {
            assertThrows<InvalidTokenException> { tokenManager.getToken() }
        }
    }

    @Test
    fun whenAccountWithWrongCredentialsAndGetTokenThenInvalidTokenExceptionIsThrown() {
        runBlocking {
            val encryptedPassword: String = encryptPassword(WRONG_PASSWORD)

            mockServerInvalidCredentialsResponse()
            accountManager.addAccountExplicitly(createTestAccount(), encryptedPassword, null)
            assertThrows<InvalidTokenException> { tokenManager.getToken() }
        }
    }

    @Test
    fun whenAccountWithNoPasswordAndGetTokenThenInvalidTokenExceptionIsThrown() {
        runBlocking {
            mockServerInvalidCredentialsResponse()
            accountManager.addAccountExplicitly(createTestAccount(), null, null)
            assertThrows<InvalidTokenException> { tokenManager.getToken() }
        }
    }

    @Test
    fun whenGetTokenAndServerReturnsErrorThenInvalidTokenExceptionIsThrown() {
        runBlocking {
            val encryptedPassword: String = encryptPassword(PASSWORD)

            mockServerFailureResponse()
            accountManager.addAccountExplicitly(createTestAccount(), encryptedPassword, null)
            assertThrows<InvalidTokenException> { tokenManager.getToken() }
        }
    }

    @Test
    fun whenAccountWithCorrectCredentialsAndTokenIsNotCachedThenGotToken() {
        runBlocking {
            val token: String = createTestAccountAndGetTokenFromServer()
            assertThat(token).matches(TOKEN)
        }
    }

    @Test
    fun whenAccountWithCorrectCredentialsAndTokenIsCachedThenGotToken() {
        runBlocking {
            createTestAccountAndGetTokenFromServer()

            mockServerFailureResponse()
            val token: String = tokenManager.getToken()
            assertThat(token).matches(TOKEN)
        }
    }

    @Test
    fun whenAccountWithCachedTokenThenGotToken() {
        runBlocking {
            createTestAccountAndSetTestToken()

            val token: String = tokenManager.getToken()
            assertThat(token).matches(TOKEN)
        }
    }

    @Test
    fun whenAccountWithCachedTokenAndGetTokenEmailAddressThenGotEmail() {
        runBlocking {
            createTestAccountAndSetTestToken()

            val email: String = tokenManager.getTokenEmailAddress()
            assertThat(email).matches(EMAIL_ADDRESS)
        }
    }

    @Test
    fun whenAccountWithCachedTokenAndGetTokenUsernameThenGotUsername() {
        runBlocking {
            createTestAccountAndSetTestToken()

            val username: String = tokenManager.getTokenUsername()
            assertThat(username).matches(USERNAME)
        }
    }

    @Test
    fun whenLogoutToLoggedAccountThenTokenAndPasswordAreRemoved() {
        runBlocking {
            createTestAccountAndSetTestToken()

            mockLogoutEndpointResponse()
            tokenManager.logout()

            val account: Account = accountManager.accounts[0]
            val token: String? = accountManager.blockingGetAuthToken(account, accountType, true)
            assertThat(token).isNull()
        }
    }

    private suspend fun createTestAccountAndGetTokenFromServer(): String {
        val encryptedPassword: String = encryptPassword(PASSWORD)

        mockServerTokenResponse()
        accountManager.addAccountExplicitly(createTestAccount(), encryptedPassword, null)

        return tokenManager.getToken()
    }

    private fun createTestAccountAndSetTestToken() {
        val encryptedPassword: String = encryptPassword(PASSWORD)
        val account: Account = createTestAccount()

        mockServerTokenResponse()
        accountManager.addAccountExplicitly(account, encryptedPassword, null)
        accountManager.setAuthToken(account, tokenType, TOKEN)

        accountManager.setUserData(account, Authenticator.USER_DATA_USERNAME, USERNAME)
    }

    private fun removeAccounts() {
        accountManager.getAccountsByType(accountType).forEach { accountManager.removeAccountExplicitly(it) }
    }

    private fun createTestAccount() : Account {
        return Account(EMAIL_ADDRESS, accountType)
    }

    private fun encryptPassword(password: String) : String {
        return Encryptor.getInstance().encrypt(password, ApplicationProvider.getApplicationContext())
    }

    private fun mockLogoutEndpointResponse() {
        mockServerJsonResponse(ServerResponse.OK)
    }
}