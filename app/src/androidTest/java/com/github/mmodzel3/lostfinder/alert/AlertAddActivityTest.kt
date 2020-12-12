package com.github.mmodzel3.lostfinder.alert

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.google.common.truth.Truth.assertThat
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


class AlertAddActivityTest : AlertEndpointTestAbstract() {
    private lateinit var alertAddScenario: ActivityScenario<AlertAddActivity>
    private lateinit var decorView: View

    @Before
    override fun setUp() {
        super.setUp()

        TokenManager.tokenManager = TokenManagerStub.getInstance()

        alertAddScenario = ActivityScenario.launch(AlertAddActivity::class.java)

        alertAddScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    @After
    override fun tearDown() {
        super.tearDown()

        alertAddScenario.close()
    }


    @Test
    fun whenAddAlertThenUserAlertIsSend() {
        mockAddAlertResponse()

        fillFields()

        onView(withId(R.id.activity_alert_add_bt_add))
                .perform(click())

        val request: RecordedRequest? = server.takeRequest(1000, TimeUnit.MILLISECONDS)
        assertThat(request).isNotNull()
        assertThat(request?.bodySize).isGreaterThan(0)
    }

    @Test
    fun whenAddAlertAndApiAccessErrorThenErrorToastIsShown() {
        mockServerFailureResponse()

        fillFields()

        onView(withId(R.id.activity_alert_add_bt_add))
                .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_alert_add_err_add_alert_api_access_problem))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenAddAlertWithInvalidCredentialsThenErrorToastIsShown() {
        mockInvalidCredentialsResponse()

        fillFields()

        onView(withId(R.id.activity_alert_add_bt_add))
                .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_alert_add_err_add_alert_invalid_token))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    private fun fillFields() {
        var titleStringArray: Array<String> = arrayOf()

        alertAddScenario.onActivity {
            titleStringArray = it.applicationContext
                    .resources.getStringArray(R.array.activity_alert_add_predefined)
        }

        onView(withId(R.id.activity_alert_add_et_description))
                .perform(ViewActions.replaceText(ALERT_DESCRIPTION))

        onView(withId(R.id.activity_alert_add_et_range))
                .perform(ViewActions.replaceText(ALERT_RANGE.toString()))

        onView(withId(R.id.activity_alert_add_sp_title))
                .perform(click())

        onData(allOf(`is`(instanceOf(String::class.java)),
                `is`(titleStringArray[1]))).inRoot(isPlatformPopup()).perform(click())

        Thread.sleep(1000)
    }
}