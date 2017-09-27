package com.tokko.cameandwentv3.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.tokko.cameandwentv3.geofence.GeofenceService

/**
 * Created by andreas on 6/07/17.
 */
class PermissionActivity: Activity() {

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.Builder(this)
                        .setTitle("GPS permissions")
                        .setMessage("This permission is required to detect when you enter or leave your working area. Without it this app will basically be a digital punch clock.")
                        .setPositiveButton("Ok") { dialog, _ -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS), 0); finish() }.show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS), 0)
            }
            return
        }
        else
            finish()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        when(requestCode){
            0 -> {
                if(grantResults!!.isNotEmpty() && grantResults.toList().stream().allMatch({x -> x == PackageManager.PERMISSION_GRANTED})) {
                    GeofenceService.initGeofences(applicationContext)
                    GeofenceService.initGeofences(this)
                }
                else
                    AlertDialog.Builder(applicationContext)
                            .setTitle("GPS permissions")
                            .setMessage("Denying these permissions will kill the core usefulness of this app.")
                            .setPositiveButton("Ok") { dialog, _ -> dialog?.dismiss() }
            }

        }
    }
}