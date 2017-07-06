package com.tokko.cameandwentv3

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.squareup.otto.Bus
import com.tokko.cameandwentv3.geofence.GeofenceService
import com.tokko.cameandwentv3.wifi.WifiReceiver

/**
 * Created by andre on 29/06/2017.
 */
class MyApplication : Application() {
    var bus : Bus = Bus()

    override fun onCreate() {
        super.onCreate()
        registerReceiver(WifiReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }
}