package com.tokko.cameandwentv3.notifications

import android.app.IntentService
import android.content.Context
import android.content.Intent

/**
 * Created by andre on 8/09/2017.
 */
class ClockoutNotification: IntentService(ClockoutNotification::class.java.canonicalName) {
    companion object {
        fun initialize(context: Context){
            context.startService(Intent(context, ClockoutNotification::class.java))
        }
    }

    override fun onHandleIntent(p0: Intent?) {

    }
}