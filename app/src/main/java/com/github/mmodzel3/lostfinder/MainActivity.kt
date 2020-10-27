package com.github.mmodzel3.lostfinder

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.github.mmodzel3.lostfinder.location.CurrentLocationBinder
import com.github.mmodzel3.lostfinder.location.CurrentLocationListener
import com.github.mmodzel3.lostfinder.location.CurrentLocationService
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.File


class MainActivity : AppCompatActivity() {
    private val OSMDROID_BASE_CACHE_DIR = "osmdroid"
    private val OSMDROID_TILE_CACHE_DIR = "tile"

    private val REQUEST_PERMISSIONS_CODE = 1
    private val MAP_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private lateinit var map : MapView
    private lateinit var mapController : IMapController
    private lateinit var currentLocationBinder : CurrentLocationBinder
    private lateinit var currentLocationMarker: Marker
    private val currentLocationConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            currentLocationBinder = service as CurrentLocationBinder
            initCurrentLocationMarker()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        requestPermissionsIfNecessary(MAP_PERMISSIONS)
        initMap()

        Intent(this, CurrentLocationService::class.java).also { intent ->
            bindService(intent, currentLocationConnection, Context.BIND_AUTO_CREATE)
        }
    }

    public override fun onResume() {
        super.onResume()
        map.onResume()
    }

    public override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if (!checkGrantedPrivileges(grantResults)) {
                    val deniedLocationMsg = R.string.denied_location_permission_msg
                    Toast.makeText(this, deniedLocationMsg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkGrantedPrivileges(grants: IntArray): Boolean {
        for (grant in grants) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    private fun initMap() {
        configMap()

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        map.setMultiTouchControls(true);
        addRotationGestureToMap()

        mapController = map.controller
        mapController.setZoom(9.5)
    }

    private fun initCurrentLocationMarker() {
        currentLocationMarker = Marker(map)
        currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(currentLocationMarker)

        currentLocationBinder.registerListener(object : CurrentLocationListener {
            override fun onLocalisationChange(location: Location) {
                val point = GeoPoint(location)
                currentLocationMarker.position = point
                mapController.setCenter(point)
            }
        })
    }

    private fun configMap() {
        val ctx: Context = applicationContext

        configMapCache()
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
    }

    private fun configMapCache() {
        val osmConfiguration = Configuration.getInstance()
        val cacheBasePath = File(cacheDir.absolutePath, OSMDROID_BASE_CACHE_DIR)
        val tileCache = File(cacheBasePath.absolutePath, OSMDROID_TILE_CACHE_DIR)

        osmConfiguration.osmdroidBasePath = cacheBasePath
        osmConfiguration.osmdroidTileCache = tileCache
    }

    private fun addRotationGestureToMap() {
        val rotationGestureOverlay = RotationGestureOverlay(map)
        rotationGestureOverlay.setEnabled(true)
        map.overlays.add(rotationGestureOverlay)
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = permissions.filter { permission ->
            (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_CODE
            )
        }
    }
}