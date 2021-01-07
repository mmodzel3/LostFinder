package com.github.mmodzel3.lostfinder.security.authentication.login

import android.accounts.Account
import android.accounts.AccountManager
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.mmodzel3.lostfinder.R
import com.google.common.truth.Truth.assertThat
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginActivityTest : LoginEndpointTestAbstract() {
    companion object {
        const val EMAIL_ADDRESS = "example@example.com"
        const val PASSWORD = "password"
        const val EMAIL_ADDRESS2 = "example2@example.com"
        const val PASSWORD2 = "password2"
        const val EMAIL_ADDRESS3 = "example3@example.com"
        const val PASSWORD3 = "password2"
        const val ONE_ELEMENT_LIST_SIZE = 1
    }

    @get:Rule var rule = ActivityScenarioRule(LoginActivity::class.java)
    private lateinit var activityScenario: ActivityScenario<LoginActivity>
    private lateinit var accountManager: AccountManager
    private lateinit var decorView: View
    private lateinit var accountType: String
    private lateinit var tokenType: String
    private val loginIdlingResource: LoginIdlingTestResource = LoginIdlingTestResource()

    @Before
    override fun setUp() {
        super.setUp()

        IdlingRegistry.getInstance().register(loginIdlingResource.idlingResource)
        activityScenario = rule.scenario

        activityScenario.onActivity {
            accountManager = AccountManager.get(it.applicationContext)
            decorView = it.window.decorView
            accountType = it.accountType
            tokenType = it.tokenType
            it.loginIdlingResource = loginIdlingResource
        }

        removeAccounts()
    }

    @After
    override fun tearDown() {
        super.tearDown()

        IdlingRegistry.getInstance().unregister(loginIdlingResource.idlingResource)
        removeAccounts()
    }

    @Test
    fun whenLoginWithCorrectCredentialsThenOldAccountsAreRemoved() {
        mockServerTokenResponse()

        addTestAccount(EMAIL_ADDRESS2, PASSWORD2)
        addTestAccount(EMAIL_ADDRESS3, PASSWORD3)

        fillFields(EMAIL_ADDRESS, PASSWORD)
        performLogin()

        val accounts: Array<out Account> = accountManager.getAccountsByType(accountType)

        assertThat(accounts).asList().hasSize(ONE_ELEMENT_LIST_SIZE)

        Thread.sleep(10000)
    }

    @Test
    fun whenLoginWithCorrectCredentialsThenAccountIsAdded() {
        mockServerTokenResponse()

        fillFields(EMAIL_ADDRESS, PASSWORD)
        performLogin()

        val account: Account = checkIfAccountExistsAndGetIt()
        assertThat(account.name).matches(EMAIL_ADDRESS)

        Thread.sleep(10000)
    }

    @Test
    fun whenLoginThenPasswordIsSaved() {
        mockServerTokenResponse()
        fillFields(EMAIL_ADDRESS, PASSWORD)
        performLogin()

        val account: Account = checkIfAccountExistsAndGetIt()
        val password: String? = accountManager.getPassword(account)
        assertThat(password).isNotEmpty()

        Thread.sleep(10000)
    }

    @Test
    fun whenLoginWithInvalidCredentialsThenErrorInvalidCredentialsToastIsShown() {
        mockServerInvalidCredentialsResponse()
        fillFields(EMAIL_ADDRESS, PASSWORD)
        performLogin()

        Thread.sleep(1000)
        onView(withText(R.string.err_login_invalid_credentials))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()))

        Thread.sleep(2000)
    }

    @Test
    fun whenLoginAndServerReturnsErrorThenServerAccessErrorToastIsShown() {
        mockServerFailureResponse()
        fillFields(EMAIL_ADDRESS, PASSWORD)
        performLogin()

        Thread.sleep(1000)
        onView(withText(R.string.err_login_access))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()))

        Thread.sleep(2000)
    }

    @Test
    fun whenLoginWithCorrectCredentialsThenGotTokenAndItIsCached() {
        mockServerTokenResponse()

        fillFields(EMAIL_ADDRESS, PASSWORD)
        performLogin()

        mockServerFailureResponse()
        val account: Account = checkIfAccountExistsAndGetIt()
        val token: String? = accountManager.blockingGetAuthToken(account, tokenType, true)
        assertThat(token).matches(TOKEN)

        Thread.sleep(10000)
    }

    @Test
    fun whenLoginWithCorrectCredentialsThenActivityIsFinished() {
        mockServerTokenResponse()
        fillFields(EMAIL_ADDRESS, PASSWORD)
        performLogin()

        activityScenario.state.isAtLeast(Lifecycle.State.DESTROYED)

        Thread.sleep(10000)
    }

    private fun checkIfAccountExistsAndGetIt() : Account {
        val accounts: Array<out Account> = accountManager.getAccountsByType(accountType)

        assertThat(accounts).isNotEmpty()
        return accounts.first()
    }

    private fun addTestAccount(emailAddress: String, password: String) {
        val account = Account(emailAddress, accountType)
        accountManager.addAccountExplicitly(account, password, null)
    }

    private fun fillFields(emailAddress: String, password: String) {
        onView(withId(R.id.activity_login_et_email_address)).perform(replaceText(emailAddress))
        onView(withId(R.id.activity_login_et_password)).perform(replaceText(password))
    }

    private fun performLogin() {
        onView(withId(R.id.activity_login_bt_login)).perform(click())
    }

    private fun removeAccounts() {
        accountManager.getAccountsByType(accountType).forEach { accountManager.removeAccountExplicitly(it) }
    }
}