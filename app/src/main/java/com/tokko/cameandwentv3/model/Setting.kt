package com.tokko.cameandwentv3.model

import android.content.Context
import com.tokko.cameandwentv3.settings.getAutomaticBreakDuration
import com.tokko.cameandwentv3.settings.getAutomaticBreakStart
import com.tokko.cameandwentv3.settings.getConsultRounding

/**
 * Created by andreas on 10/07/17.
 */
class Setting {
    var automaticBreak: Long = 30
    var automaticBreakStart: Long = 0
    var consultRounding: Boolean = true

    fun init(context: Context) {
        automaticBreak = context.getAutomaticBreakDuration()
        automaticBreakStart = context.getAutomaticBreakStart()
        consultRounding = context.getConsultRounding()
    }
}