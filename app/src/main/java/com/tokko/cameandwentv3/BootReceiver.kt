package com.tokko.cameandwentv3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by andreas on 18/08/17.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0!!.startActivity(Intent(p0, LoginActivity::class.java))
    }
}