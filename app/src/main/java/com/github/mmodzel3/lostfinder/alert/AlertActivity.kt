package com.github.mmodzel3.lostfinder.alert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.chat.ChatActivity
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus


open class AlertActivity : AppCompatActivity() {
    private val alertEndpoint: AlertEndpoint by lazy {
        AlertEndpointFactory.createAlertEndpoint(TokenManager.getInstance(applicationContext))
    }

    private val alertEndpointViewModel: AlertEndpointViewModel by viewModels {
        AlertEndpointViewModelFactory(alertEndpoint)
    }

    private lateinit var recyclerView: RecyclerView

    private lateinit var alertAdapter: AlertAdapter
    private lateinit var tokenManager: TokenManager
    private lateinit var alertEndpointViewModelDataObserver: Observer<in MutableMap<String, Alert>>
    private lateinit var alertEndpointViewModelStatusObserver: Observer<ServerEndpointStatus>

    private var firstFetchAlerts = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_alert)

        tokenManager = TokenManager.getInstance(applicationContext)

        recyclerView = findViewById(R.id.activity_alert_rv_alert_list)

        alertAdapter = AlertAdapter(tokenManager)

        initRecyclerView()
        observeAlertEndpointViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()

        alertEndpointViewModel.alerts.removeObserver(alertEndpointViewModelDataObserver)
        alertEndpointViewModel.status.removeObserver(alertEndpointViewModelStatusObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_alert, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        return if (id == R.id.activity_alert_it_map) {
            goToMapActivity()
            true
        } else if (id == R.id.activity_alert_it_chat) {
            goToChatActivity()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = alertAdapter
    }

    private fun observeAlertEndpointViewModel() {
        observeAlertEndpointViewModelData()
        observeAlertEndpointViewModelStatus()
    }

    private fun observeAlertEndpointViewModelData() {
        alertEndpointViewModelDataObserver = Observer {
            alertAdapter.alerts = it.values.toMutableList()
            alertAdapter.notifyDataSetChanged()

            if (firstFetchAlerts) {
                recyclerView.scrollToPosition(0)
                firstFetchAlerts = false
            }
        }

        alertEndpointViewModel.alerts.observe(this, alertEndpointViewModelDataObserver)
    }

    private fun observeAlertEndpointViewModelStatus() {
        val activity: Activity = this

        alertEndpointViewModelStatusObserver = Observer {
            if (it == ServerEndpointStatus.ERROR) {
                Toast.makeText(activity, R.string.activity_alert_err_fetching_alerts_api_access_problem,
                        Toast.LENGTH_LONG).show()
            } else if (it == ServerEndpointStatus.INVALID_TOKEN) {
                Toast.makeText(activity, R.string.activity_alert_err_fetching_alerts_invalid_token,
                        Toast.LENGTH_LONG).show()
                goToLoginActivity()
            }
        }

        alertEndpointViewModel.status.observe(this, alertEndpointViewModelStatusObserver)
    }

    private fun goToMapActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    private fun goToChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
        finish()
    }
}