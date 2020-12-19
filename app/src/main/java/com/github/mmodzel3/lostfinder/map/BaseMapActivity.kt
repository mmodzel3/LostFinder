package com.github.mmodzel3.lostfinder.map

import android.Manifest
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.alert.AlertActivity
import com.github.mmodzel3.lostfinder.chat.ChatActivity
import com.github.mmodzel3.lostfinder.permissions.AppCompactActivityWithPermissionsRequest
import com.github.mmodzel3.lostfinder.weather.WeatherActivity
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.File


open class BaseMapActivity :
        AppCompactActivityWithPermissionsRequest(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            R.string.denied_location_permission_msg) {
    private val OSMDROID_BASE_CACHE_DIR = "osmdroid"
    private val OSMDROID_TILE_CACHE_DIR = "tile"

    private val MAP_DEFAULT_ZOOM = 19.5

    protected lateinit var map : MapView
    protected lateinit var mapController : IMapController

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    protected open fun initMap() {
        configMap()

        map = findViewById(R.id.activity_map_map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        map.setMultiTouchControls(true);
        addRotationGestureToMap()

        mapController = map.controller
        mapController.setZoom(MAP_DEFAULT_ZOOM)
    }

    protected open fun deInitMap() {

    }

    private fun configMap() {
        configMapCache()
        loadMapConfig()
    }

    private fun configMapCache() {
        val osmConfiguration = Configuration.getInstance()
        val cacheBasePath = File(cacheDir.absolutePath, OSMDROID_BASE_CACHE_DIR)
        val tileCache = File(cacheBasePath.absolutePath, OSMDROID_TILE_CACHE_DIR)

        osmConfiguration.osmdroidBasePath = cacheBasePath
        osmConfiguration.osmdroidTileCache = tileCache
    }

    private fun loadMapConfig() {
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
    }

    private fun addRotationGestureToMap() {
        val rotationGestureOverlay = RotationGestureOverlay(map)
        rotationGestureOverlay.isEnabled = true
        map.overlays.add(rotationGestureOverlay)
    }
}