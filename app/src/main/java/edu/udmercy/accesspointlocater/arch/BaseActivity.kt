package edu.udmercy.accesspointlocater.arch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import edu.udmercy.accesspointlocater.R

open class BaseActivity: AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
        private const val REQUEST_CODE = 5883
        private const val FOREGROUND_REQUEST_CODE = 5883
        private const val BACKGROUND_REQUEST_CODE = 6447
    }

    open fun startFunction() {}

    private val foregroundPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private val missingPermissions = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkForegroundPermission()
    }

    private fun checkForegroundPermission(){
        for(permission in foregroundPermissions){
            if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                Log.i(TAG, "checkPermission: Missing permission: $permission")
                missingPermissions.add(permission)
            }
        }
        if(missingPermissions.isNotEmpty()){
            val arrayPermissions = missingPermissions.toTypedArray()
            Log.i(TAG, "checkPermission: Requesting Permissions")
            ActivityCompat.requestPermissions(this,
                arrayPermissions, FOREGROUND_REQUEST_CODE
            )
        } else {
            startFunction()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FOREGROUND_REQUEST_CODE -> {
                // Checking whether user granted the permission or not.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startFunction()
                }
                else {
                    Toast.makeText(this,"Denied", Toast.LENGTH_SHORT).show()
                }
            }
            BACKGROUND_REQUEST_CODE -> {
                // Checking whether user granted the permission or not.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startFunction()
                }
                else {
                    Toast.makeText(this,"Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}