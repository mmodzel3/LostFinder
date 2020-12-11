package com.github.mmodzel3.lostfinder.alert

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*

class AlertAdapterTest : AlertEndpointTestAbstract() {
    companion object {
        const val DAY_BEFORE_IN_MILLISECONDS = 24 * 60 * 60 * 1000
        const val MINUTE_IN_MILLISECONDS = 60 * 1000
    }

    private lateinit var parentView: View
    private lateinit var alertAdapter: AlertAdapter

    @Before
    override fun setUp() {
        super.setUp()

        mockView()

        alertAdapter = AlertAdapter(TokenManagerStub.getInstance())

        alertAdapter.alerts = alerts
    }

    @Test
    fun whenOnBindViewHolderOnAlertThenGotUpdatedData() {
        val holder = AlertViewHolder(parentView)
        alertAdapter.onBindViewHolder(holder, alerts.size-1)

        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS - MINUTE_IN_MILLISECONDS)
        assertThat(holder.userName).isEqualTo(ALERT_USER_NAME)
        assertThat(holder.title).isEqualTo(ALERT_TITLE)
        assertThat(holder.description).isEqualTo(ALERT_DESCRIPTION)
        assertThat(holder.range).isEqualTo(ALERT_RANGE)
        assertThat(holder.location.longitude).isEqualTo(ALERT_LONGITUDE)
        assertThat(holder.location.latitude).isEqualTo(ALERT_LATITUDE)
        assertThat(holder.time).isAtLeast(yesterday)
    }

    private fun mockView() {
        val context: Context = Mockito.mock(Context::class.java)
        parentView = Mockito.mock(View::class.java)

        `when`(parentView.context).thenReturn(context)
        `when`(context.getString(Mockito.anyInt())).thenReturn("km")
        `when`(parentView.findViewById<TextView>(Mockito.anyInt())).thenAnswer {
            val textView: TextView = Mockito.mock(TextView::class.java)
            var text = ""

            `when`(textView.text).then {
                return@then text
            }

            `when`(textView.setText(Mockito.anyString())).then {
                text = it.arguments[0] as String
                return@then Unit
            }

            return@thenAnswer textView
        }
    }
}