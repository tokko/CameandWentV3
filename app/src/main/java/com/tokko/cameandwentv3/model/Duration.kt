package com.tokko.cameandwentv3.model

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by andreas on 6/07/17.
 */
class Duration(logs: Collection<LogEntry>, val consultRounding: Boolean, val clean: Boolean = false) {
    var duration: String = ""
    get() {
        var duration = this.logs.fold(0L) { a, x -> a + if(x.entered) x.timestamp else -x.timestamp }
        if(logs.size%2 != 0) duration -= System.currentTimeMillis()
        duration = Math.abs(duration)
        if(consultRounding)
            duration = (((duration+millisIn30Minutes)/millisIn30Minutes)*millisIn30Minutes)
        return duration.toHourMinuteSeconds()
    }
    var logs: ArrayList<LogEntry> = ArrayList(logs.sortedBy { it.timestamp })
    var date: String = ""
        get() = SimpleDateFormat("yyyy-MM-dd").format(Date(logs.first().timestamp))
    val millisIn30Minutes = 30*60*1000

    init{
        if(clean)
            clean()
    }
    fun clean() : Iterable<LogEntry> {
        val stack = Stack<LogEntry>()
        val toRemove = ArrayList<LogEntry>()
        toRemove.addAll(logs.sortedBy { it.timestamp }.takeWhile { !it.entered })
        stack.addAll(logs.sortedBy { x -> x.timestamp }.asIterable())

        while (!stack.empty()) {
            val top = stack.pop()
            if (stack.empty()) break
            if (top.entered == stack.peek().entered)
                toRemove.add(top)
        }
        val cleanedList = ArrayList(logs)
        cleanedList.removeAll(toRemove)
        if (cleanedList.size == 1 && !cleanedList[0].entered) {
            toRemove.add(cleanedList[0])
            cleanedList.clear()
        }
        logs = ArrayList(cleanedList)
        return toRemove
    }
}

fun Long.toHourMinuteSeconds(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this - TimeUnit.HOURS.toMillis(hours))
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun Long.toHourMinute(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this - TimeUnit.HOURS.toMillis(hours))
    return String.format("%02d:%02d", hours, minutes)
}