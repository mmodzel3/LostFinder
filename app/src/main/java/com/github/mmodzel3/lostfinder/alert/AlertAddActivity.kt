package com.github.mmodzel3.lostfinder.alert

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.chat.ChatActivity
import com.github.mmodzel3.lostfinder.location.CurrentLocationBinder
import com.github.mmodzel3.lostfinder.location.CurrentLocationListener
import com.github.mmodzel3.lostfinder.location.CurrentLocationService
import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.UserRole
import kotlinx.coroutines.launch
import java.util.*

class AlertAddActivity : AppCompatActivity() {
    private lateinit var currentLocationBinder : CurrentLocationBinder
    private lateinit var currentLocationConnection : ServiceConnection

    private var currentLocation: Location? = null

    private val alertEndpoint: AlertEndpoint by lazy {
        AlertEndpointFactory.createAlertEndpoint(TokenManager.getInstance(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_alert_add)

        bindToCurrentLocationService()
        bindAlertTitles()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_alert_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        return if (id == R.id.activity_alert_add_it_map) {
            goToMapActivity()
            true
        } else if (id == R.id.activity_alert_add_it_chat) {
            goToChatActivity()
            true
        } else if (id == R.id.activity_alert_add_it_alert) {
            goToAlertActivity()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun bindToCurrentLocationService() {
        currentLocationConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                currentLocationBinder = service as CurrentLocationBinder
                listenToCurrentLocation()
                initSendButton()
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }

        Intent(this, CurrentLocationService::class.java).also { intent ->
            bindService(intent, currentLocationConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun initSendButton() {
        val sendButton: Button = findViewById(R.id.activity_alert_add_bt_add)
        sendButton.setOnClickListener {
            onAddClick()
        }

        enableAddButton()
    }

    private fun onAddClick() {
        val titleSpinner: Spinner = findViewById(R.id.activity_alert_add_sp_title)
        val descriptionEditText: EditText = findViewById(R.id.activity_alert_add_et_description)
        val rangeEditText: EditText = findViewById(R.id.activity_alert_add_et_range)

        val titleId: Int = titleSpinner.selectedItemPosition
        val description: String = descriptionEditText.text.toString()
        val type: AlertType = AlertTypeTitleConverter.getAlertTypeFromTitleId(titleId)
        val range: Double = rangeEditText.text.toString().toDouble()
        val sendDate: Date = Date()

        val userAlert = UserAlert(type, currentLocation, range,
                description, sendDate)

        disableAddButton()
        lifecycleScope.launch {
            addUserAlert(userAlert)
        }
    }

    private fun enableAddButton() {
        val addButton: Button = findViewById(R.id.activity_alert_add_bt_add)
        addButton.isEnabled = true
    }

    private fun disableAddButton() {
        val addButton: Button = findViewById(R.id.activity_alert_add_bt_add)
        addButton.isEnabled = false
    }

    private fun listenToCurrentLocation() {
        currentLocationBinder.registerListener(object : CurrentLocationListener {
            override fun onLocalisationChange(location: android.location.Location) {
                currentLocation = Location(location.longitude, location.latitude)
            }
        })
    }

    private suspend fun addUserAlert(userAlert: UserAlert) {
        try {
            alertEndpoint.addAlert(userAlert)

            Toast.makeText(this, R.string.activity_alert_add_msg_add_alert_success,
                    Toast.LENGTH_SHORT).show()

            finish()
        } catch (e: AlertEndpointAccessErrorException) {
            Toast.makeText(this, R.string.activity_alert_add_err_add_alert_api_access_problem,
                    Toast.LENGTH_LONG).show()

            enableAddButton()
        } catch (e: InvalidTokenException) {
            Toast.makeText(this, R.string.activity_alert_add_err_add_alert_invalid_token,
                    Toast.LENGTH_LONG).show()

            goToLoginActivity()
        }
    }

    private fun bindAlertTitles() {
        val tokenManager: TokenManager = TokenManager.getInstance(applicationContext)

        when (tokenManager.getTokenRole()) {
            UserRole.USER -> {
                val userTitleStringArray = applicationContext.resources
                        .getStringArray(R.array.activity_alert_add_predefined_user)
                bindAlertTitles(userTitleStringArray)
            }
            UserRole.MANAGER -> {
                val userTitleStringArray = applicationContext.resources
                        .getStringArray(R.array.activity_alert_add_predefined_manager)
                bindAlertTitles(userTitleStringArray)
            }
            else -> {
                val userTitleStringArray = applicationContext.resources
                        .getStringArray(R.array.activity_alert_add_predefined_owner)
                bindAlertTitles(userTitleStringArray)
            }
        }
    }

    private fun bindAlertTitles(titlesStringArray: Array<String>) {
        val titleSpinner: Spinner = findViewById(R.id.activity_alert_add_sp_title)
        val spinnerArrayAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                titlesStringArray)

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        titleSpinner.adapter = spinnerArrayAdapter
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

    private fun goToAlertActivity() {
        val intent = Intent(this, AlertActivity::class.java)
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