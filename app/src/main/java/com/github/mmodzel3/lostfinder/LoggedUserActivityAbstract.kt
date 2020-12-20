package com.github.mmodzel3.lostfinder

import android.accounts.AccountManager
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.alert.AlertActivity
import com.github.mmodzel3.lostfinder.chat.ChatActivity
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.logout.LogoutEndpoint
import com.github.mmodzel3.lostfinder.security.authentication.logout.LogoutEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.security.authentication.logout.LogoutEndpointFactory
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.weather.WeatherActivity
import kotlinx.coroutines.launch

abstract class LoggedUserActivityAbstract : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
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
        } else if (id == R.id.activity_toolbar_it_logout) {
            logout()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
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

    protected fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)
        finish()
    }

    private fun logout() {
        val tokenManager: TokenManager = TokenManager.getInstance(applicationContext)

        lifecycleScope.launch {
            try {
                tokenManager.logout()
                goToLoginActivity()

                Toast.makeText(this@LoggedUserActivityAbstract,
                        R.string.activity_toolbar_logout_msg_success, Toast.LENGTH_LONG).show()
            } catch (e: LogoutEndpointAccessErrorException) {
                Toast.makeText(this@LoggedUserActivityAbstract,
                        R.string.activity_toolbar_logout_err_api_access_error, Toast.LENGTH_LONG).show()
            }
        }
    }
}