package com.tokko.cameandwentv3

import android.app.Application
import com.squareup.otto.Bus
import com.tokko.cameandwentv3.geofence.GeofenceService

/**
 * Created by andre on 29/06/2017.
 */
class MyApplication : Application() {
    var bus : Bus = Bus()

    override fun onCreate() {
        super.onCreate()
    }
}