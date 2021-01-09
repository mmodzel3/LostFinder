package com.github.mmodzel3.lostfinder.security.authentication.register

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.mmodzel3.lostfinder.R
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterActivityTest : RegisterRepositoryTestAbstract() {
    companion object {
        const val EMAIL_ADDRESS = "example@example.com"
        const val PASSWORD = "password"
        const val SERVER_PASSWORD = "server_password"
        const val WRONG_SERVER_PASSWORD = "wrong_server_password"
        const val TOO_SHORT_PASSWORD = "1"
        const val INVALID_EMAIL_ADDRESS = "example."
        const val USERNAME = "user123"
    }

    @get:Rule var rule = ActivityScenarioRule(RegisterActivity::class.java)
    private lateinit var activityScenario: ActivityScenario<RegisterActivity>
    private lateinit var decorView: View

    @Before
    override fun setUp() {
        super.setUp()

        activityScenario = rule.scenario

        activityScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    @Test
    fun whenRegisterWithFilledDataThenAccountIsRegisteredAndActivityIsFinishing() {
        mockServerRegisterResponse()

        fillFields(EMAIL_ADDRESS, PASSWORD, SERVER_PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        assertThat(activityScenario.state).isEqualTo(Lifecycle.State.DESTROYED)
    }

    @Test
    fun whenRegisterWithDuplicatedDataThenErrorToastIsShown() {
        mockServerDuplicatedResponse()

        fillFields(EMAIL_ADDRESS, PASSWORD, SERVER_PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_duplicated))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun whenRegisterWithNotFilledEmailThenErrorToastIsShown() {
        mockServerRegisterResponse()

        fillFields("", PASSWORD, SERVER_PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_blank_fields))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun whenRegisterWithNotFilledPasswordThenErrorToastIsShown() {
        mockServerRegisterResponse()

        fillFields(EMAIL_ADDRESS, "", SERVER_PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_blank_fields))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun whenRegisterWithNotFilledUsernameThenErrorToastIsShown() {
        mockServerRegisterResponse()

        fillFields(EMAIL_ADDRESS, PASSWORD, SERVER_PASSWORD, "")
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_blank_fields))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun whenRegisterAndInvalidServerPasswordThenErrorToastIsShown() {
        mockServerInvalidPasswordResponse()

        fillFields(EMAIL_ADDRESS, PASSWORD, WRONG_SERVER_PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_invalid_server_password))
            .inRoot(withDecorView(Matchers.not(decorView)))
            .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun whenRegisterAndApiAccessErrorThenErrorToastIsShown() {
        mockServerFailureResponse()

        fillFields(EMAIL_ADDRESS, PASSWORD, SERVER_PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_api_access_problem))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun whenRegisterAndInvalidParamThenErrorToastIsShown() {
        mockPasswordTooShortResponse()

        fillFields(EMAIL_ADDRESS, TOO_SHORT_PASSWORD, SERVER_PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_password_too_short))
            .inRoot(withDecorView(Matchers.not(decorView)))
            .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun whenRegisterWithInvalidEmailThenErrorToastIsShown() {
        mockPasswordTooShortResponse()

        fillFields(INVALID_EMAIL_ADDRESS, PASSWORD, SERVER_PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_invalid_email))
            .inRoot(withDecorView(Matchers.not(decorView)))
            .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    private fun fillFields(emailAddress: String, password: String, serverPassword: String, username: String) {
        onView(withId(R.id.activity_register_et_email_address)).perform(replaceText(emailAddress))
        onView(withId(R.id.activity_register_et_password)).perform(replaceText(password))
        onView(withId(R.id.activity_register_et_server_password)).perform(replaceText(password))
        onView(withId(R.id.activity_register_et_username)).perform(replaceText(username))
    }

    private fun performRegister() {
        onView(withId(R.id.activity_register_bt_register)).perform(click())
    }
}