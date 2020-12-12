package com.github.mmodzel3.lostfinder.alert

import android.view.View
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test

class AlertActivityTest : AlertEndpointTestAbstract() {
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
}