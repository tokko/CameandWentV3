package com.tokko.cameandwentv3.model

/**
 * Created by andreas on 10/07/17.
 */
class Setting {
    var automaticBreakDuration: Long = 30 * 60 * 1000
    var automaticBreakStart: Long = 11*60*60*1000 + 30*60*1000
    var consultRounding: Boolean = true
    var automaticBreakStartFormatted = ""
        get() = automaticBreakStart.toHourMinute()
    var automaticBreakDurationFormatted = ""
        get() = automaticBreakDuration.toHourMinute()
}