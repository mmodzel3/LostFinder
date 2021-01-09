package com.github.mmodzel3.lostfinder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.alert.AlertActivity
import com.github.mmodzel3.lostfinder.alert.AlertRepository
import com.github.mmodzel3.lostfinder.chat.ChatActivity
import com.github.mmodzel3.lostfinder.chat.ChatRepository
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.logout.LogoutEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.settings.SettingsActivity
import com.github.mmodzel3.lostfinder.user.*
import com.github.mmodzel3.lostfinder.weather.WeatherActivity
import com.github.mmodzel3.lostfinder.weather.WeatherRepository
import kotlinx.coroutines.launch

abstract class LoggedUserActivityAbstract : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(TokenManager.getInstance(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        closeOptionsMenu()

        return if (id == R.id.activity_toolbar_it_map) {
            goToMapActivity()
            true
        } else if (id == R.id.activity_toolbar_it_chat) {
            goToChatActivity()
            true
        } else if (id == R.id.activity_toolbar_it_alert) {
            goToAlertActivity()
            true
        } else if (id == R.id.activity_toolbar_it_weather) {
            goToWeatherActivity()
            true
        } else if (id == R.id.activity_toolbar_it_user) {
            goToUserActivity()
            true
        } else if (id == R.id.activity_toolbar_it_settings) {
            goToSettingsActivity()
            true
        } else if (id == R.id.activity_toolbar_it_logout) {
            logout()
            true
        } else if (id == R.id.activity_toolbar_it_close) {
            closeApplication()
            true
        } else if (id == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        goToMapActivity()
    }

    protected fun goToMapActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    protected fun goToChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    protected fun goToAlertActivity() {
        val intent = Intent(this, AlertActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    protected fun goToWeatherActivity() {
        val intent = Intent(this, WeatherActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    protected fun goToUserActivity() {
        val intent = Intent(this, UserActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    protected fun goToSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    protected fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        AlertRepository.clear()
        ChatRepository.clear()
        UserRepository.clear()
        WeatherRepository.clear()

        startActivity(intent)
        finish()
    }

    private fun logout() {
        val tokenManager: TokenManager = TokenManager.getInstance(applicationContext)

        lifecycleScope.launch {
            try {
                val fullLogout: Boolean = tokenManager.logout()

                if (fullLogout) {
                    Toast.makeText(this@LoggedUserActivityAbstract,
                        R.string.activity_logout_msg_success, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@LoggedUserActivityAbstract,
                        R.string.activity_logout_msg_partial_success, Toast.LENGTH_LONG).show()
                }

                goToLoginActivity()
            } catch (e: LogoutEndpointAccessErrorException) {
                Toast.makeText(this@LoggedUserActivityAbstract,
                    R.string.activity_logout_err_api_access_error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun closeApplication() {
        lifecycleScope.launch {
            try {
                userViewModel.clearUserLocation()

                Toast.makeText(this@LoggedUserActivityAbstract,
                    R.string.activity_close_msg_success, Toast.LENGTH_LONG).show()

                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)

                finish()
            } catch (e: UserEndpointAccessErrorException) {
                Toast.makeText(this@LoggedUserActivityAbstract,
                    R.string.activity_close_err_api_access_error, Toast.LENGTH_LONG).show()
            } catch (e: InvalidTokenException) {
                Toast.makeText(this@LoggedUserActivityAbstract,
                    R.string.activity_close_err_invalid_token, Toast.LENGTH_LONG).show()

                goToLoginActivity()
            }
        }
    }
}