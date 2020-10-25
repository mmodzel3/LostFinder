package com.github.mmodzel3.lostfinder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.File


class MainActivity : AppCompatActivity() {
    private val OSMDROID_BASE_CACHE_DIR = "osmdroid"
    private val OSMDROID_TILE_CACHE_DIR = "tile"

    private val REQUEST_PERMISSIONS_CODE = 1
    private val MAP_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private val DENIED_MAP_PERMISSIONS_MSG =
            "Without localisation permission application will not work properly. " +
                    "Restart application and accept requested permission."

    private var map : MapView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        requestPermissionsIfNecessary(MAP_PERMISSIONS)
        initMap()
    }

    public override fun onResume() {
        super.onResume()
        map?.onResume()
    }

    public override fun onPause() {
        super.onPause()
        map?.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if (!checkGrantedPrivileges(grantResults)) {
                    Toast.makeText(this, DENIED_MAP_PERMISSIONS_MSG, Toast.LENGTH_LONG)
                            .show()
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
        map?.setTileSource(TileSourceFactory.MAPNIK)

        map?.setMultiTouchControls(true);

        val mapController = map?.controller
        mapController?.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController?.setCenter(startPoint)
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

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = permissions.filter { permission ->
            (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toTypedArray(),
                    REQUEST_PERMISSIONS_CODE)
        }
    }
}