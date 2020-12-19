package com.github.mmodzel3.lostfinder.alert

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.LoggedUserActivityAbstract
import com.github.mmodzel3.lostfinder.location.CurrentLocationBinder
import com.github.mmodzel3.lostfinder.location.CurrentLocationListener
import com.github.mmodzel3.lostfinder.location.CurrentLocationService
import com.github.mmodzel3.lostfinder.map.ChooseLocationMapActivity
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.UserRole
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

class AlertAddActivity : LoggedUserActivityAbstract() {
    companion object {
        const val DEFAULT_RANGE = 180.0
        const val CHOOSE_LOCATION_CODE = 1
    }

    private lateinit var currentLocationBinder : CurrentLocationBinder
    private lateinit var currentLocationConnection : ServiceConnection

    private var currentLocation: Location? = null
    private var currentLocationRange: Double = DEFAULT_RANGE

    private val alertEndpoint: AlertEndpoint by lazy {
        AlertEndpointFactory.createAlertEndpoint(TokenManager.getInstance(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_alert_add)

        initChooseLocationButton()
        bindToCurrentLocationService()
        bindAlertTitles()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSE_LOCATION_CODE && resultCode == RESULT_OK && data != null) {
            val chosenLocationLongitude = data.getDoubleExtra(ChooseLocationMapActivity.LOCATION_LONGITUDE_INTENT, 0.0)
            val chosenLocationLatitude = data.getDoubleExtra(ChooseLocationMapActivity.LOCATION_LATITUDE_INTENT, 0.0)

            setAlertLocationText(chosenLocationLongitude, chosenLocationLatitude)
        }
    }

    private fun bindToCurrentLocationService() {
        currentLocationConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                currentLocationBinder = service as CurrentLocationBinder
                listenToCurrentLocation()
                initAddButton()
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }

        Intent(this, CurrentLocationService::class.java).also { intent ->
            bindService(intent, currentLocationConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun initAddButton() {
        val addButton: Button = findViewById(R.id.activity_alert_add_bt_add)
        addButton.setOnClickListener {
            onAddClick()
        }

        enableAddButton()
    }

    private fun initChooseLocationButton() {
        val chooseLocationButton: ImageButton = findViewById(R.id.activity_alert_add_bt_choose_location)
        chooseLocationButton.setOnClickListener {
            goToChooseLocationActivity()
        }
    }

    private fun onAddClick() {
        val titleSpinner: Spinner = findViewById(R.id.activity_alert_add_sp_title)
        val descriptionEditText: EditText = findViewById(R.id.activity_alert_add_et_description)
        val longitudeEditText: EditText = findViewById(R.id.activity_alert_add_et_longitude)
        val latitudeEditText: EditText = findViewById(R.id.activity_alert_add_et_latitude)
        val rangeEditText: EditText = findViewById(R.id.activity_alert_add_et_range)

        val titleId: Int = titleSpinner.selectedItemPosition
        val type: AlertType = AlertTypeTitleConverter.getAlertTypeFromTitleId(titleId)
        val description: String = descriptionEditText.text.toString()

        val rangeText: String = rangeEditText.text.toString().trim()
        val range: Double = if (rangeText.isNotEmpty()) rangeText.toDouble() else currentLocationRange

        val longitudeText: String = longitudeEditText.text.toString().trim()
        val longitude: Double? = if (longitudeText.isNotEmpty()) longitudeText.toDouble()
                                    else currentLocation?.longitude

        val latitudeText: String = latitudeEditText.text.toString().trim()
        val latitude: Double? = if (latitudeText.isNotEmpty()) latitudeText.toDouble()
                                    else currentLocation?.latitude

        val location = if (longitude != null && latitude != null)
                            com.github.mmodzel3.lostfinder.location.Location(longitude, latitude)
                        else null

        val sendDate = Date()
        val userAlert = UserAlert(type, location, range, description, sendDate)

        disableAddButton()
        lifecycleScope.launch {
            addUserAlert(userAlert)
        }
    }

    private fun listenToCurrentLocation() {
        currentLocationBinder.registerListener(object : CurrentLocationListener {
            override fun onLocalisationChange(location: Location) {
                currentLocation = location
                currentLocationRange = if (location.hasAccuracy()) location.accuracy.toDouble()
                else currentLocationRange

                setAlertLocationHint(currentLocation!!.longitude, currentLocation!!.latitude)
                setAlertRangeHint(currentLocationRange)
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

    private fun setAlertLocationText(longitude: Double, latitude: Double) {
        val longitudeEditText: EditText = findViewById(R.id.activity_alert_add_et_longitude)
        val latitudeEditText: EditText = findViewById(R.id.activity_alert_add_et_latitude)

        longitudeEditText.setText(longitude.toString(), TextView.BufferType.EDITABLE)
        latitudeEditText.setText(latitude.toString(), TextView.BufferType.EDITABLE)
    }

    private fun setAlertLocationHint(longitude: Double, latitude: Double) {
        val longitudeEditText: EditText = findViewById(R.id.activity_alert_add_et_longitude)
        val latitudeEditText: EditText = findViewById(R.id.activity_alert_add_et_latitude)

        longitudeEditText.hint = longitude.toString()
        latitudeEditText.hint = latitude.toString()
    }

    private fun setAlertRangeHint(range: Double) {
        val rangeEditText: EditText = findViewById(R.id.activity_alert_add_et_range)
        rangeEditText.hint = ((range * 100).roundToInt() / 100).toString()
    }

    private fun enableAddButton() {
        val addButton: Button = findViewById(R.id.activity_alert_add_bt_add)
        addButton.isEnabled = true
    }

    private fun disableAddButton() {
        val addButton: Button = findViewById(R.id.activity_alert_add_bt_add)
        addButton.isEnabled = false
    }

    private fun goToChooseLocationActivity() {
        val intent = Intent(this, ChooseLocationMapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivityForResult(intent, CHOOSE_LOCATION_CODE)
    }
}