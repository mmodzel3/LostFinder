package com.github.mmodzel3.lostfinder.security.authentication.authenticator

import android.accounts.Account
import android.accounts.AccountManager
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginAccountManagerTestAbstract
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginInvalidCredentialsException
import com.github.mmodzel3.lostfinder.security.encryption.Decryptor
import com.github.mmodzel3.lostfinder.security.encryption.Encryptor
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Encryptor::class, Decryptor::class, AccountManager::class)
@PowerMockIgnore("javax.net.ssl.*")
class AuthenticatorTest : LoginAccountManagerTestAbstract() {
    private lateinit var authenticator: Authenticator

    @Before
    override fun setUp() {
        super.setUp()
        authenticator = Authenticator(context)
    }

    @Test
    fun whenRetrieveTokenAndTokenIsCachedThenGotSavedTokenWithoutServerRequest() {
        mockServerFailureResponse()
        saveTokenInAccountManager()

        val account: Account = createTestAccount()

        runBlocking {
            val token = authenticator.retrieveAccountAuthToken(account, TOKEN_TYPE)
            assertThat(token).matches(TOKEN)
        }
    }

    @Test
    fun whenRetrieveTokenWithCorrectPasswordAndTokenIsNotCachedThenGotTokenFromServer() {
        mockServerTokenResponse()

        val account: Account = createTestAccount()
        setAccountPassword()

        runBlocking {
            val token = authenticator.retrieveAccountAuthToken(account, TOKEN_TYPE)
            assertThat(token).matches(TOKEN)
        }
    }

    @Test
    fun whenRetrieveTokenWithWrongPasswordAndTokenIsNotCachedThenThrowLoginInvalidCredentials() {
        mockServerInvalidCredentialsResponse()

        val account: Account = createTestAccount()
        setAccountPassword()

        runBlocking {
            assertThrows<LoginInvalidCredentialsException>
                { authenticator.retrieveAccountAuthToken(account, TOKEN_TYPE) }
        }
    }

    @Test
    fun whenRetrieveTokenAndTokenIsNotCachedAndServerReturnsErrorThenThrowLoginEndpointAccessError() {
        mockServerFailureResponse()

        val account: Account = createTestAccount()
        setAccountPassword()

        runBlocking {
            assertThrows<LoginEndpointAccessErrorException>
            { authenticator.retrieveAccountAuthToken(account, TOKEN_TYPE) }
        }
    }
}