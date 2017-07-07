package com.tokko.cameandwentv3

import android.app.Application
import com.squareup.otto.Bus

/**
 * Created by andre on 29/06/2017.
 */
class MyApplication : Application() {
    var bus : Bus = Bus()

    override fun onCreate() {
        super.onCreate()
    }
}