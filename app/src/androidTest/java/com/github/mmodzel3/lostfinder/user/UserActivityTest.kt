package com.github.mmodzel3.lostfinder.user

import android.view.View
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.google.common.truth.Truth.assertThat
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class UserActivityTest : UserRepositoryTestAbstract() {
    private lateinit var userScenario: ActivityScenario<UserActivity>
    private lateinit var decorView: View

    @Before
    override fun setUp() {
        super.setUp()

        TokenManager.tokenManager = TokenManagerStub.getInstance()
    }

    @After
    override fun tearDown() {
        super.tearDown()

        userScenario.close()
    }

    @Test
    fun whenOpenActivityThenUsersFromServerAreShown() {
        startActivityNormally()

        userScenario.onActivity {
            val recyclerView: RecyclerView = it.findViewById(R.id.activity_user_rv_user_list)

            assertThat(recyclerView.size).isEqualTo(users.size)
        }
    }

    @Test
    fun whenOpenActivityAndApiAccessErrorThenErrorToastIsShown() {
        startActivityWithApiAccessError()

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_err_fetching_api_access_error))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenOpenActivityWithInvalidCredentialsThenErrorToastIsShown() {
        startActivityWithInvalidCredentials()

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_err_fetching_invalid_token))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenClickButtonInRecyclerViewThenRequestIsSend() {
        startActivityNormally()

        mockUserManagementResponse()

        onView(withId(R.id.activity_user_rv_user_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                                clickOnViewChild(R.id.activity_user_info_bt_delete_account)))

        val request: RecordedRequest? = server.takeRequest(1000, TimeUnit.MILLISECONDS)
        assertThat(request).isNotNull()
    }

    @Test
    fun whenClickedButtonInRecyclerViewWithInvalidPermissionThenErrorToastIsShown() {
        startActivityNormally()

        mockUserManagementInvalidPermissionResponse()
        onView(withId(R.id.activity_user_rv_user_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    clickOnViewChild(R.id.activity_user_info_bt_delete_account)))

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_err_invalid_permission))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenClickedButtonInRecyclerViewForNotExistingUserThenErrorToastIsShown() {
        startActivityNormally()

        mockUserManagementNotFoundResponse()
        onView(withId(R.id.activity_user_rv_user_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    clickOnViewChild(R.id.activity_user_info_bt_delete_account)))

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_err_not_found))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }


    @Test
    fun whenClickedButtonInRecyclerViewWithApiErrorProblemThenErrorToastIsShown() {
        startActivityNormally()

        mockServerFailureResponse()
        onView(withId(R.id.activity_user_rv_user_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    clickOnViewChild(R.id.activity_user_info_bt_delete_account)))

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_err_api_access_problem))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenClickedButtonInRecyclerViewWithInvalidCredentialsThenErrorToastIsShown() {
        startActivityNormally()

        mockInvalidCredentialsResponse()
        onView(withId(R.id.activity_user_rv_user_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                                clickOnViewChild(R.id.activity_user_info_bt_delete_account)))

        Thread.sleep(1000)
        onView(withText(R.string.activity_user_err_invalid_token))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    private fun startActivityNormally() {
        mockGetAllUsersResponse()

        userScenario = ActivityScenario.launch(UserActivity::class.java)
        server.takeRequest()

        userScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    private fun startActivityWithApiAccessError() {
        mockServerFailureResponse()

        userScenario = ActivityScenario.launch(UserActivity::class.java)
        server.takeRequest()

        userScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    private fun startActivityWithInvalidCredentials() {
        mockInvalidCredentialsResponse()

        userScenario = ActivityScenario.launch(UserActivity::class.java)
        server.takeRequest()

        userScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    private fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null
        override fun getDescription() = "Click on a child view with specified id."
        override fun perform(uiController: UiController, view: View) = click().perform(uiController, view.findViewById<View>(viewId))
    }
}