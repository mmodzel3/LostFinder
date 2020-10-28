package com.github.mmodzel3.lostfinder.permissions

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

open class AppCompactActivityWithPermissionsRequest(val permissions : Array<String>,
                                                   val deniedPermissionsMsg: Int) :
        AppCompatActivity() {

    private val REQUEST_PERMISSIONS_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionsIfNecessary(permissions)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if (!isPermissionsGranted(grantResults)) {
                    Toast.makeText(this, deniedPermissionsMsg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun isPermissionsGranted(grants: IntArray): Boolean {
        for (grant in grants) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
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