package com.tokko.cameandwentv3.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager


/**
 * Created by andreas on 6/07/17.
 */
class WifiReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        val ssid = info.ssid.replace("\"", "")
        context.onWifiConnected(ssid)
    }
}