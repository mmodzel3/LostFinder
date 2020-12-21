package com.github.mmodzel3.lostfinder.user

import android.view.View
import androidx.core.view.size
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.alert.AlertEndpointTestAbstract
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.google.common.truth.Truth.assertThat
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers.isEmptyString
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class UserChangePasswordActivityTest : UserEndpointTestAbstract() {
    companion object {
        const val TEST_NEW_PASSWORD = "new_password"
        const val TEST_OLD_PASSWORD = "old_password"
        const val TEST_INVALID_PASSWORD = TEST_NEW_PASSWORD + "1"
    }

    private lateinit var userChangePasswordScenario: ActivityScenario<UserChangePasswordActivity>
    private lateinit var decorView: View

    @Before
    override fun setUp() {
        super.setUp()

        TokenManager.tokenManager = TokenManagerStub.getInstance()
        userChangePasswordScenario = ActivityScenario.launch(UserChangePasswordActivity::class.java)

        userChangePasswordScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    @After
    override fun tearDown() {
        super.tearDown()

        userChangePasswordScenario.close()
    }

    @Test
    fun whenChangePasswordWithCorrectOldPasswordThenActivitIsFinished() {
        mockSuccessChangePasswordResponse()
        fillFields(TEST_OLD_PASSWORD, TEST_NEW_PASSWORD, TEST_NEW_PASSWORD)

        onView(withId(R.id.activity_user_change_password_bt_change_password))
            .perform(click())

        Thread.sleep(1000)

        assertThat(userChangePasswordScenario.state).isEqualTo(Lifecycle.State.DESTROYED)
    }

    @Test
    fun whenChangePasswordWithWrongPasswordThenErrorToastIsShown() {
        mockInvalidOldPasswordChangePasswordResponse()
        fillFields(TEST_INVALID_PASSWORD, TEST_NEW_PASSWORD, TEST_NEW_PASSWORD)

        onView(withId(R.id.activity_user_change_password_bt_change_password))
            .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_change_password_err_invalid_old_password))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenChangePasswordWithWrongRepeatedNewPasswordThenErrorToastIsShown() {
        fillFields(TEST_OLD_PASSWORD, TEST_NEW_PASSWORD, TEST_INVALID_PASSWORD)

        onView(withId(R.id.activity_user_change_password_bt_change_password))
            .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_change_password_err_not_same_new_passwords))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenChangePasswordAndApiAccessProblemThenErrorToastIsShown() {
        mockServerFailureResponse()
        fillFields(TEST_OLD_PASSWORD, TEST_NEW_PASSWORD, TEST_NEW_PASSWORD)

        onView(withId(R.id.activity_user_change_password_bt_change_password))
            .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_change_password_err_api_access_error))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenChangePasswordAndInvalidTokenThenErrorToastIsShown() {
        mockInvalidCredentialsResponse()
        fillFields(TEST_OLD_PASSWORD, TEST_NEW_PASSWORD, TEST_NEW_PASSWORD)

        onView(withId(R.id.activity_user_change_password_bt_change_password))
            .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_change_password_err_invalid_token))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    private fun fillFields(oldPassword: String, newPassword: String, repeatedNewPassword: String) {
        onView(withId(R.id.activity_user_change_password_et_old_password))
            .perform(replaceText(oldPassword))

        onView(withId(R.id.activity_user_change_password_et_new_password))
            .perform(replaceText(newPassword))

        onView(withId(R.id.activity_user_change_password_et_repeated_new_password))
            .perform(replaceText(repeatedNewPassword))
    }
}