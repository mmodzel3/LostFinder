package com.github.mmodzel3.lostfinder.map

import android.view.MenuItem
import android.view.SubMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.alert.Alert
import com.github.mmodzel3.lostfinder.alert.AlertTypeTitleConverter
import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.User
import com.google.android.material.navigation.NavigationView

open class DataLocationsWithNavDrawerMapActivity : DataLocationsMapActivity() {
    private val tokenManager by lazy { TokenManager.getInstance(applicationContext) }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigation: NavigationView
    private lateinit var navigationDrawerToggle: ActionBarDrawerToggle

    private lateinit var alertsSubMenu : SubMenu
    private lateinit var usersSubMenu : SubMenu

    private val subMenuLock = Any()

    private var alertsNavigationItemLocationMap: MutableMap<MenuItem, Location> = HashMap()
    private var usersNavigationItemLocationMap: MutableMap<MenuItem, Location> = HashMap()

    override fun initMap() {
        super.initMap()

        initNavigationDrawer()
    }

    private fun initNavigationDrawer() {
        drawerLayout = findViewById(R.id.activity_map_drawer_layout)
        navigation = findViewById(R.id.activity_map_navigation)

        initNavigationDrawerButton()
        initNavigationSubMenus()

        observeAlertsUpdates()
        observeUsersUpdates()
        listenToNavigationMenuItemsClicks()
    }

    private fun initNavigationDrawerButton() {
        navigationDrawerToggle = ActionBarDrawerToggle(this, drawerLayout,
                R.string.activity_map_navigation_drawer_open, R.string.activity_map_navigation_drawer_close)

        drawerLayout.addDrawerListener(navigationDrawerToggle)
        navigationDrawerToggle.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initNavigationSubMenus() {
        alertsSubMenu = navigation.menu.addSubMenu(R.string.activity_map_navigation_drawer_alerts_title)
        usersSubMenu = navigation.menu.addSubMenu(R.string.activity_map_navigation_drawer_users_title)
    }

    private fun observeAlertsUpdates() {
        alertEndpointViewModel.alerts.observe(this, Observer {
            updateAlertsNavigationMenuItems(it)
        })
    }

    private fun observeUsersUpdates() {
        userEndpointViewModel.users.observe(this, Observer {
            updateUsersNavigationMenuItems(it)
        })
    }

    private fun updateAlertsNavigationMenuItems(alerts: Map<String, Alert>) = synchronized(subMenuLock) {
        alertsSubMenu.clear()
        alertsNavigationItemLocationMap = HashMap()

        alerts.values.filter { it.location != null }.forEach {
            val alertTitle: String = AlertTypeTitleConverter.convertAlertTypeToTitle(this, it.type) +
                    " [" + it.user.username + "]"

            val item: MenuItem = alertsSubMenu.add(alertTitle)
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_yellow_alert)

            alertsNavigationItemLocationMap[item] = it.location!!
        }
    }

    private fun updateUsersNavigationMenuItems(users: Map<String, User>) = synchronized(subMenuLock) {
        usersSubMenu.clear()
        usersNavigationItemLocationMap = HashMap()

        users.values.filter { it.location != null && it.email != tokenManager.getTokenEmailAddress() }.forEach {
            val item: MenuItem = usersSubMenu.add(it.username)
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_user)

            usersNavigationItemLocationMap[item] = it.location!!
        }
    }

    private fun listenToNavigationMenuItemsClicks() {
        navigation.setNavigationItemSelectedListener { item ->
            onNavigationItemClick(item)
            true
        }
    }

    private fun onNavigationItemClick(item: MenuItem) = synchronized(subMenuLock) {
        when (item) {
            in alertsNavigationItemLocationMap -> {
                val location: Location = alertsNavigationItemLocationMap[item]!!
                mapController.animateTo(location.toGeoPoint())
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            in usersNavigationItemLocationMap -> {
                val location: Location = usersNavigationItemLocationMap[item]!!
                mapController.animateTo(location.toGeoPoint())
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            else -> { }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (navigationDrawerToggle.onOptionsItemSelected(item)) true
            else super.onOptionsItemSelected(item)
    }
}