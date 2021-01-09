package com.github.mmodzel3.lostfinder.alert

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

class AlertActivityTest : AlertRepositoryTestAbstract() {
    private lateinit var alertScenario: ActivityScenario<AlertActivity>
    private lateinit var decorView: View

    @Before
    override fun setUp() {
        super.setUp()

        TokenManager.tokenManager = TokenManagerStub.getInstance()
    }

    @After
    override fun tearDown() {
        super.tearDown()

        alertScenario.close()
    }

    @Test
    fun whenOpenActivityThenAlertsFromServerAreShown() {
        startActivityNormally()

        alertScenario.onActivity {
            val recyclerView: RecyclerView = it.findViewById(R.id.activity_alert_rv_alert_list)

            assertThat(recyclerView.size).isEqualTo(alerts.size)
        }
    }

    @Test
    fun whenOpenActivityAndApiAccessErrorThenErrorToastIsShown() {
        startActivityWithApiAccessError()

        Thread.sleep(1000)
        onView(withText(R.string.activity_alert_err_fetching_alerts_api_access_problem))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenOpenActivityWithInvalidCredentialsThenErrorToastIsShown() {
        startActivityWithInvalidCredentials()

        Thread.sleep(1000)
        onView(withText(R.string.activity_alert_err_fetching_alerts_invalid_token))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenEndAlertInRecyclerViewThenEndAlertIsSend() {
        startActivityNormally()

        mockEndAlertResponse()
        onView(withId(R.id.activity_alert_rv_alert_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                                clickOnViewChild(R.id.activity_alert_info_bt_end_alert)))

        val request: RecordedRequest? = server.takeRequest(1000, TimeUnit.MILLISECONDS)
        assertThat(request).isNotNull()
    }

    @Test
    fun whenEndAlertInRecyclerViewWithInvalidPermissionThenErrorToastIsShown() {
        startActivityNormally()

        mockEndAlertInvalidPermissionResponse()
        onView(withId(R.id.activity_alert_rv_alert_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    clickOnViewChild(R.id.activity_alert_info_bt_end_alert)))

        Thread.sleep(1000)
        onView(withText(R.string.activity_alert_err_end_alert_invalid_permission))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenEndAlertInRecyclerViewThatDoesNotExistThenErrorToastIsShown() {
        startActivityNormally()

        mockEndAlertNotFoundResponse()
        onView(withId(R.id.activity_alert_rv_alert_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    clickOnViewChild(R.id.activity_alert_info_bt_end_alert)))

        Thread.sleep(1000)
        onView(withText(R.string.activity_alert_err_end_alert_not_found))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenEndAlertInRecyclerViewWithApiErrorProblemThenErrorToastIsShown() {
        startActivityNormally()

        mockServerFailureResponse()
        onView(withId(R.id.activity_alert_rv_alert_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                                clickOnViewChild(R.id.activity_alert_info_bt_end_alert)))

        Thread.sleep(1000)
        onView(withText(R.string.activity_alert_err_end_alert_api_access_problem))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenEndAlertInRecyclerViewWithInvalidCredentialsThenErrorToastIsShown() {
        startActivityNormally()

        mockInvalidCredentialsResponse()
        onView(withId(R.id.activity_alert_rv_alert_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                                clickOnViewChild(R.id.activity_alert_info_bt_end_alert)))

        Thread.sleep(1000)
        onView(withText(R.string.activity_alert_err_end_alert_invalid_token))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    private fun startActivityNormally() {
        mockGetActiveAlertsResponse()

        alertScenario = ActivityScenario.launch(AlertActivity::class.java)
        server.takeRequest()

        alertScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    private fun startActivityWithApiAccessError() {
        mockServerFailureResponse()

        alertScenario = ActivityScenario.launch(AlertActivity::class.java)
        server.takeRequest()

        alertScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    private fun startActivityWithInvalidCredentials() {
        mockInvalidCredentialsResponse()

        alertScenario = ActivityScenario.launch(AlertActivity::class.java)
        server.takeRequest()

        alertScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    private fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null
        override fun getDescription() = "Click on a child view with specified id."
        override fun perform(uiController: UiController, view: View) = click().perform(uiController, view.findViewById<View>(viewId))
    }
}