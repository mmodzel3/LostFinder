package com.github.mmodzel3.lostfinder.security.authentication.register

import android.accounts.Account
import android.accounts.AccountManager
import android.view.View
import android.widget.Checkable
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.mmodzel3.lostfinder.R
import com.google.common.truth.Truth.assertThat
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterActivityTest : RegisterEndpointTestAbstract() {
    companion object {
        const val EMAIL_ADDRESS = "example@example.com"
        const val PASSWORD = "password"
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

        fillFields(EMAIL_ADDRESS, PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        assertThat(activityScenario.state).isEqualTo(Lifecycle.State.DESTROYED)
    }

    @Test
    fun whenRegisterWithDuplicatedDataThenErrorToastIsShown() {
        mockServerDuplicatedResponse()

        fillFields(EMAIL_ADDRESS, PASSWORD, USERNAME)
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

        fillFields("", PASSWORD, USERNAME)
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

        fillFields(EMAIL_ADDRESS, "", USERNAME)
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

        fillFields(EMAIL_ADDRESS, PASSWORD, "")
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_blank_fields))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun whenRegisterAndApiAccessErrorThenErrorToastIsShown() {
        mockServerFailureResponse()

        fillFields(EMAIL_ADDRESS, PASSWORD, USERNAME)
        performRegister()

        Thread.sleep(1000)
        onView(withText(R.string.activity_register_err_api_access_problem))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    private fun fillFields(emailAddress: String, password: String, username: String) {
        onView(withId(R.id.activity_register_et_email_address)).perform(replaceText(emailAddress))
        onView(withId(R.id.activity_register_et_password)).perform(replaceText(password))
        onView(withId(R.id.activity_register_et_username)).perform(replaceText(username))
    }

    private fun performRegister() {
        onView(withId(R.id.activity_register_bt_register)).perform(click())
    }
}