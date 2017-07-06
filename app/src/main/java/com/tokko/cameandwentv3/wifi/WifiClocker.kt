package com.tokko.cameandwentv3.wifi

import android.app.IntentService
import android.content.Intent
import android.widget.Toast

/**
 * Created by andreas on 6/07/17.
 */
class WifiClocker : IntentService("WifiClocker") {
    override fun onHandleIntent(intent: Intent?) {
        Toast.makeText(this, "WIFI CHANGED", Toast.LENGTH_SHORT).show()
    }
}