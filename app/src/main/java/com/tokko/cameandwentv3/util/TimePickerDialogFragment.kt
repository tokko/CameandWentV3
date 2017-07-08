package com.tokko.cameandwentv3.util

import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import org.joda.time.DateTime

/**
 * Created by andre on 8/07/2017.
 */
class TimePickerDialogFragment : DialogFragment() {
    companion object {
        val RESULT_TIME = "time"
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val currentTime = DateTime(System.currentTimeMillis())
        val dialog = TimePickerDialog(activity, {_, hour, minute ->
            val i = hour * 60 * 60 * 1000 + minute * 60 * 1000
            targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent().putExtra(RESULT_TIME, i.toLong()))
        }, currentTime.hourOfDay, currentTime.minuteOfHour, true)
        return dialog
    }
}