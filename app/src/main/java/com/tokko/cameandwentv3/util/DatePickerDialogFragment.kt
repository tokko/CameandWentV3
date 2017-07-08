package com.tokko.cameandwentv3.util

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import org.joda.time.DateTime

/**
 * Created by andre on 8/07/2017.
 */
class DatePickerDialogFragment: DialogFragment() {
    companion object {
        val RESULT_DATE = "result date"
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = DatePickerDialog(activity)
        dialog.setOnDateSetListener { _, year, month, day ->
            val dateMillis = DateTime(year, month, day, 0, 0, 0).millis
            targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent().putExtra(RESULT_DATE, dateMillis))
            dismiss()
        }
        return dialog
    }
}