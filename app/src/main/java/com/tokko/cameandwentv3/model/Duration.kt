package com.tokko.cameandwentv3.model

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by andreas on 6/07/17.
 */
class Duration {
    constructor(logs: Collection<LogEntry>){
        this.logs = ArrayList<LogEntry>(logs)
        date = SimpleDateFormat("yyyy:MM:dd").format(Date(logs.first().timestamp))
        this.logs.sumBy { x -> if(x.entered) x.timestamp else -x.timestamp }
        val duration = System.currentTimeMillis()
        val hours = TimeUnit.MILLISECONDS.toHours(duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours))
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
        this.duration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    var duration: String
    var logs: ArrayList<LogEntry>
    var date: String

    fun <T> ArrayList<T>.sumBy(sumFunction: (T) -> Long) : Long{
        var sum: Long = 0
        this.forEach{t-> sum += sumFunction(t)}
        return sum
    }
}