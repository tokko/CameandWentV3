package com.tokko.cameandwentv3.model

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by andreas on 6/07/17.
 */
class Duration(logs: Collection<LogEntry>) {
    var duration: String
    var logs: ArrayList<LogEntry> = ArrayList(logs)
    var date: String = SimpleDateFormat("yyyy-MM-dd").format(Date(logs.first().timestamp))

    init {
        var duration = this.logs.fold(0L) { a, x -> a + if(x.entered) x.timestamp else -x.timestamp }
        if(logs.size%2 != 0) duration -= System.currentTimeMillis()
        duration = Math.abs(duration)
        val hours = TimeUnit.MILLISECONDS.toHours(duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours))
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
        this.duration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}