package com.tokko.cameandwentv3.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimeTickReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.startService(Intent(p0, CountdownNotificationService::class.java).setAction(CountdownNotificationService.ACTION_UPDATE))
    }
}