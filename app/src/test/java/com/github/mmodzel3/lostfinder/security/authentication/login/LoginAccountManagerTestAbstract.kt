package com.github.mmodzel3.lostfinder.security.authentication.login

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import com.github.mmodzel3.lostfinder.security.authentication.encryption.DecryptorStub
import com.github.mmodzel3.lostfinder.security.authentication.encryption.EncryptorStub
import com.github.mmodzel3.lostfinder.security.encryption.Decryptor
import com.github.mmodzel3.lostfinder.security.encryption.Encryptor
import com.github.mmodzel3.lostfinder.security.encryption.EncryptorInterface
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox

@RunWith(PowerMockRunner::class)
@PrepareForTest(Encryptor::class, Decryptor::class, AccountManager::class)
@PowerMockIgnore("javax.net.ssl.*")
abstract class LoginAccountManagerTestAbstract : LoginRepositoryTestAbstract() {
    companion object {
        const val EMAIL_ADDRESS = "example@example.com"
        const val PASSWORD = "password"
        const val ACCOUNT_TYPE = "type"
        const val TOKEN_TYPE = "token"
    }

    protected lateinit var accountManager: AccountManager
    protected lateinit var context: Context

    @Before
    override fun setUp() {
        super.setUp()

        accountManager = mockAccountManager()
        context = mockContext()
        mockCryptor()
    }

    protected fun createTestAccount() : Account {
        val account: Account = Mockito.mock(Account::class.java)

        Whitebox.setInternalState(account, "name", EMAIL_ADDRESS)
        Whitebox.setInternalState(account, "type", ACCOUNT_TYPE)

        return account
    }

    protected fun setAccountPassword(password: String) {
        val encryptor: EncryptorInterface = Encryptor.getInstance()
        val encryptedPassword: String = encryptor.encrypt(password, context)

        Mockito.`when`(accountManager.getPassword(Mockito.any())).thenReturn(encryptedPassword)
    }

    protected fun setAccountPassword() {
        setAccountPassword(PASSWORD)
    }

    protected fun saveTokenInAccountManager() {
        Mockito.`when`(accountManager.peekAuthToken(Mockito.any(), Mockito.anyString()))
            .thenReturn(TOKEN)
    }

    private fun mockAccountManager() : AccountManager {
        val accountManager: AccountManager = Mockito.mock(AccountManager::class.java)
        PowerMockito.mockStatic(AccountManager::class.java)

        Mockito.`when`(AccountManager.get(ArgumentMatchers.any())).thenReturn(accountManager)
        return accountManager
    }

    private fun mockContext() : Context {
        return Mockito.mock(Context::class.java)
    }

    private fun mockCryptor() {
        mockEncryptor()
        mockDecryptor()
    }

    private fun mockDecryptor() {
        PowerMockito.mockStatic(Decryptor::class.java)
        val companionMock: Decryptor.Companion = PowerMockito.mock(Decryptor.Companion::class.java)
        Whitebox.setInternalState(
            Decryptor::class.java, "Companion",
            companionMock
        )

        PowerMockito.`when`(Decryptor.Companion.getInstance()).thenReturn(DecryptorStub())
    }

    private fun mockEncryptor() {
        PowerMockito.mockStatic(Encryptor::class.java)
        val companionMock: Encryptor.Companion = PowerMockito.mock(Encryptor.Companion::class.java)
        Whitebox.setInternalState(
            Encryptor::class.java, "Companion",
            companionMock
        )

        PowerMockito.`when`(Encryptor.Companion.getInstance()).thenReturn(EncryptorStub())
    }
}