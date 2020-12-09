package com.github.mmodzel3.lostfinder.security.authentication.authenticator

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import com.github.mmodzel3.lostfinder.notification.PushNotificationService
import com.github.mmodzel3.lostfinder.security.authentication.encryption.DecryptorStub
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
import org.mockito.Mockito.`when`
import com.nhaarman.mockitokotlin2.any
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox

@RunWith(PowerMockRunner::class)
@PrepareForTest(Encryptor::class, Decryptor::class, AccountManager::class, PushNotificationService::class)
@PowerMockIgnore("javax.net.ssl.*")
class AuthenticatorTest : LoginAccountManagerTestAbstract() {
    companion object {
        const val PUSH_NOTIFICATION_DEST_TOKEN = "notification_token"
    }

    private lateinit var authenticator: Authenticator

    @Before
    override fun setUp() {
        super.setUp()

        authenticator = Authenticator(context)
        mockPushNotificationService()
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

    private fun mockPushNotificationService() {
        PowerMockito.mockStatic(PushNotificationService::class.java)
        val companionMock: PushNotificationService.Companion = PowerMockito.mock(PushNotificationService.Companion::class.java)
        Whitebox.setInternalState(
            PushNotificationService::class.java, "Companion",
            companionMock
        )

        PowerMockito.`when`(PushNotificationService.Companion.getNotificationDestToken(any()))
            .thenReturn(PUSH_NOTIFICATION_DEST_TOKEN)
    }
}