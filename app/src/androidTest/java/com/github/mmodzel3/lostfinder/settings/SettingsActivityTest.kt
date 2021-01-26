package com.github.mmodzel3.lostfinder.settings

import android.content.Context
import android.view.View
import android.widget.Checkable
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.github.mmodzel3.lostfinder.user.UserRepositoryTestAbstract
import com.google.common.truth.Truth.assertThat
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test

class SettingsActivityTest : UserRepositoryTestAbstract() {
    private lateinit var settingsScenario: ActivityScenario<SettingsActivity>
    private lateinit var decorView: View
    private lateinit var tokenManager: TokenManager

    @Before
    override fun setUp() {
        super.setUp()

        tokenManager = TokenManagerStub.getInstance()
        TokenManager.tokenManager = tokenManager

        startActivity()
    }

    @After
    override fun tearDown() {
        super.tearDown()

        settingsScenario.close()
    }

    @Test
    fun whenOpenActivityThenGotCorrectUserData() {
        settingsScenario.onActivity {
            val usernameTextView: TextView = it.findViewById(R.id.activity_settings_tv_username)
            val emailTextView: TextView = it.findViewById(R.id.activity_settings_tv_email)

            assertThat(usernameTextView.text).isEqualTo(tokenManager.getTokenUsername())
            assertThat(emailTextView.text).isEqualTo(tokenManager.getTokenEmailAddress())
        }
    }

    @Test
    fun whenDeleteAccountThenItIsDeleted() {
        mockDeleteAccountResponse()

        onView(withId(R.id.activity_settings_bt_delete_account))
            .perform(click())

        onView(withText(R.string.yes))
            .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_settings_msg_delete_account_success))
            .inRoot(RootMatchers.withDecorView(Matchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenDeleteAccountWithApiAccessErrorThenErrorToastIsShown() {
        mockInvalidCredentialsResponse()

        onView(withId(R.id.activity_settings_bt_delete_account))
            .perform(click())

        onView(withText(R.string.yes))
            .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_settings_err_delete_account_invalid_token))
            .inRoot(RootMatchers.withDecorView(Matchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenDeleteAccountWithInvalidTokenThenErrorToastIsShown() {
        mockInvalidCredentialsResponse()

        onView(withId(R.id.activity_settings_bt_delete_account))
            .perform(click())

        onView(withText(R.string.yes))
            .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_settings_err_delete_account_invalid_token))
            .inRoot(RootMatchers.withDecorView(Matchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenStopSharingLocationThenItIsStopped() {
        mockClearUserLocationResponse()

        onView(withId(R.id.activity_settings_sw_sharing_location))
            .perform(setChecked(true))

        onView(withId(R.id.activity_settings_sw_sharing_location))
            .perform(setChecked(false))

        Thread.sleep(1000)
        settingsScenario.onActivity {
            val settingsSharedPreferences = it.getSharedPreferences(SettingsActivity.SETTINGS, Context.MODE_PRIVATE)
            assertThat(settingsSharedPreferences.getBoolean(SettingsActivity.SHARE_LOCATION, true))
                .isEqualTo(false)
        }

        Thread.sleep(2000)
    }

    @Test
    fun whenStartSharingLocationThenItIsStarted() {
        mockClearUserLocationResponse()

        onView(withId(R.id.activity_settings_sw_sharing_location))
            .perform(setChecked(true))

        Thread.sleep(1000)
        settingsScenario.onActivity {
            val settingsSharedPreferences = it.getSharedPreferences(SettingsActivity.SETTINGS, Context.MODE_PRIVATE)
            assertThat(settingsSharedPreferences.getBoolean(SettingsActivity.SHARE_LOCATION, false))
                .isEqualTo(true)
        }

        Thread.sleep(2000)
    }

    @Test
    fun whenStopSharingLocationWithApiAccessErrorThenErrorToastIsShown() {
        mockServerFailureResponse()

        onView(withId(R.id.activity_settings_sw_sharing_location))
            .perform(setChecked(true))

        onView(withId(R.id.activity_settings_sw_sharing_location))
            .perform(setChecked(false))

        Thread.sleep(1000)

        onView(withText(R.string.activity_settings_err_stop_sharing_location_api_access_problem))
            .inRoot(RootMatchers.withDecorView(Matchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenStopSharingLocationWithInvalidTokenThenErrorToastIsShown() {
        mockInvalidCredentialsResponse()

        onView(withId(R.id.activity_settings_sw_sharing_location))
            .perform(setChecked(true))

        onView(withId(R.id.activity_settings_sw_sharing_location))
            .perform(setChecked(false))

        Thread.sleep(1000)
        onView(withText(R.string.activity_settings_err_stop_sharing_location_invalid_token))
            .inRoot(RootMatchers.withDecorView(Matchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Thread.sleep(2000)
    }

    private fun setChecked(checked: Boolean) = object : ViewAction {
        val checkableViewMatcher = object : BaseMatcher<View>() {
            override fun matches(item: Any?): Boolean = CoreMatchers.isA(Checkable::class.java).matches(item)
            override fun describeTo(description: Description?) {
                description?.appendText("is Checkable instance ")
            }
        }

        override fun getConstraints(): BaseMatcher<View> = checkableViewMatcher
        override fun getDescription(): String? = null
        override fun perform(uiController: UiController?, view: View) {
            val checkableView: Checkable = view as Checkable
            checkableView.isChecked = checked
        }
    }

    private fun startActivity() {
        settingsScenario = ActivityScenario.launch(SettingsActivity::class.java)

        settingsScenario.onActivity {
            decorView = it.window.decorView
        }
    }
}