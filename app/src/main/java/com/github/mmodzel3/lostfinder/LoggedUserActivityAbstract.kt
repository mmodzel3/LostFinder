package com.github.mmodzel3.lostfinder

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.mmodzel3.lostfinder.alert.AlertActivity
import com.github.mmodzel3.lostfinder.chat.ChatActivity
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.weather.WeatherActivity

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
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
        finish()
    }
}