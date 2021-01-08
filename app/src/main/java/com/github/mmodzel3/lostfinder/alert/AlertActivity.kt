package com.github.mmodzel3.lostfinder.alert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.LoggedUserActivityAbstract
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.github.mmodzel3.lostfinder.server.ServerResponse
import kotlinx.coroutines.launch

open class AlertActivity : LoggedUserActivityAbstract() {
    private val tokenManager: TokenManager by lazy {
        TokenManager.getInstance(applicationContext)
    }

    private val alertViewModel: AlertViewModel by viewModels {
        AlertViewModelFactory(tokenManager)
    }

    private lateinit var recyclerView: RecyclerView

    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertViewModelDataObserver: Observer<in MutableMap<String, Alert>>
    private lateinit var alertViewModelStatusObserver: Observer<ServerEndpointStatus>

    private var firstFetchAlerts = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_alert)

        recyclerView = findViewById(R.id.activity_alert_rv_alert_list)

        alertAdapter = AlertAdapter(tokenManager)

        initRecyclerView()
        initAddButton()
        observealertViewModel()
        observeEndAlert()
    }

    override fun onResume() {
        super.onResume()

        alertViewModel.runUpdates()
    }

    override fun onPause() {
        super.onPause()

        alertViewModel.stopUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()

        alertViewModel.alerts.removeObserver(alertViewModelDataObserver)
        alertViewModel.status.removeObserver(alertViewModelStatusObserver)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = alertAdapter
    }

    private fun observealertViewModel() {
        observealertViewModelData()
        observealertViewModelStatus()
    }

    private fun observealertViewModelData() {
        alertViewModelDataObserver = Observer {
            alertAdapter.alerts = it.values.toMutableList()
            alertAdapter.notifyDataSetChanged()

            if (firstFetchAlerts) {
                recyclerView.scrollToPosition(0)
                firstFetchAlerts = false
            }
        }

        alertViewModel.alerts.observe(this, alertViewModelDataObserver)
    }

    private fun observealertViewModelStatus() {
        val activity: Activity = this

        alertViewModelStatusObserver = Observer {
            if (it == ServerEndpointStatus.ERROR) {
                Toast.makeText(activity, R.string.activity_alert_err_fetching_alerts_api_access_problem,
                        Toast.LENGTH_LONG).show()
            } else if (it == ServerEndpointStatus.INVALID_TOKEN) {
                Toast.makeText(activity, R.string.activity_alert_err_fetching_alerts_invalid_token,
                        Toast.LENGTH_LONG).show()
                goToLoginActivity()
            }
        }

        alertViewModel.status.observe(this, alertViewModelStatusObserver)
    }

    private fun observeEndAlert() {
        alertAdapter.setOnEndAlertListener(object : EndAlertListener {
            override fun onEndAlert(alertId: String) {
                onEndAlertInRecyclerView(alertId)
            }
        })
    }

    private fun onEndAlertInRecyclerView(alertId: String) {
        val activity: Activity = this
        lifecycleScope.launch {
            try {
                val response: ServerResponse = alertViewModel.endAlert(alertId)

                if (response == ServerResponse.INVALID_PERMISSION) {
                    Toast.makeText(activity, R.string.activity_alert_err_end_alert_invalid_permission,
                        Toast.LENGTH_LONG).show()
                } else if (response == ServerResponse.NOT_FOUND) {
                    Toast.makeText(activity, R.string.activity_alert_err_end_alert_not_found,
                        Toast.LENGTH_LONG).show()
                }
            } catch (e: AlertEndpointAccessErrorException) {
                Toast.makeText(activity, R.string.activity_alert_err_end_alert_api_access_problem,
                        Toast.LENGTH_LONG).show()
            } catch (e: InvalidTokenException) {
                Toast.makeText(activity, R.string.activity_alert_err_end_alert_invalid_token,
                    Toast.LENGTH_LONG).show()
                goToLoginActivity()
            }
        }
    }

    private fun initAddButton() {
        val addButton: ImageButton = findViewById(R.id.activity_alert_btn_add)

        addButton.setOnClickListener {
            onAddButtonClick()
        }
    }

    private fun onAddButtonClick() {
        val intent = Intent(this, AlertAddActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }
}