package com.github.mmodzel3.lostfinder.alert

import android.view.View
import android.widget.Spinner
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
import com.github.mmodzel3.lostfinder.user.UserRole
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
    }

    @After
    override fun tearDown() {
        super.tearDown()

        alertAddScenario.close()
    }


    @Test
    fun whenAddAlertThenUserAlertIsSend() {
        startActivity()
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
        startActivity()
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
        startActivity()
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

    @Test
    fun whenAddAlertIsShownForUserRoleThenCorrectSpinnerTitleListIsShown() {
        startActivity(UserRole.USER)

        alertAddScenario.onActivity {
            val titleSpinner: Spinner = it.findViewById(R.id.activity_alert_add_sp_title)
            val titleStringArrayCount: Int = titleSpinner.adapter.count
            val expectedTitleStringArrayCount = it.resources
                    .getStringArray(R.array.activity_alert_add_predefined_user).size

            assertThat(titleStringArrayCount).isEqualTo(expectedTitleStringArrayCount)
        }
    }

    @Test
    fun whenAddAlertIsShownForManagerRoleThenCorrectSpinnerTitleListIsShown() {
        startActivity(UserRole.MANAGER)

        alertAddScenario.onActivity {
            val titleSpinner: Spinner = it.findViewById(R.id.activity_alert_add_sp_title)
            val titleStringArrayCount: Int = titleSpinner.adapter.count
            val expectedTitleStringArrayCount = it.resources
                    .getStringArray(R.array.activity_alert_add_predefined_manager).size

            assertThat(titleStringArrayCount).isEqualTo(expectedTitleStringArrayCount)
        }
    }

    @Test
    fun whenAddAlertIsShownForOwnerRoleThenCorrectSpinnerTitleListIsShown() {
        startActivity(UserRole.MANAGER)

        alertAddScenario.onActivity {
            val titleSpinner: Spinner = it.findViewById(R.id.activity_alert_add_sp_title)
            val titleStringArrayCount: Int = titleSpinner.adapter.count
            val expectedTitleStringArrayCount = it.resources
                    .getStringArray(R.array.activity_alert_add_predefined_owner).size

            assertThat(titleStringArrayCount).isEqualTo(expectedTitleStringArrayCount)
        }
    }

    private fun fillFields() {
        var titleStringArray: Array<String> = arrayOf()

        alertAddScenario.onActivity {
            titleStringArray = it.applicationContext
                    .resources.getStringArray(R.array.activity_alert_add_predefined_user)
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

    private fun startActivity(userRole: UserRole = UserRole.OWNER) {
        TokenManager.tokenManager = TokenManagerStub.getInstance(userRole = userRole)
        alertAddScenario = ActivityScenario.launch(AlertAddActivity::class.java)

        alertAddScenario.onActivity {
            decorView = it.window.decorView
        }
    }
}