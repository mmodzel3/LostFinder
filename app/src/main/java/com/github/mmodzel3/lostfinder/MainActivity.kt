package com.github.mmodzel3.lostfinder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_CODE = 1
    private val MAP_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val DENIED_MAP_PERMISSIONS_MSG =
            "Without write and localisation permissions application will not work properly. " +
                    "Restart application and accept requested permissions."

    private var map : MapView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

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
        map = findViewById(R.id.map)
        map?.setTileSource(TileSourceFactory.MAPNIK)

        val mapController = map?.controller
        mapController?.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController?.setCenter(startPoint)
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