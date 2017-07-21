package com.tokko.cameandwentv3.model

import android.content.Context

/**
 * Created by andreas on 10/07/17.
 */
class Setting {
    var automaticBreak: Long = 30*60*1000
    var automaticBreakStart: Long = 11*60*60*1000 + 30*60*1000
    var consultRounding: Boolean = true
}