package com.tokko.cameandwentv3.settings

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.events.EventSettingsChanged
import com.tokko.cameandwentv3.getBus
import com.tokko.cameandwentv3.model.Setting
import com.tokko.cameandwentv3.model.toHourMinute
import com.tokko.cameandwentv3.util.TimePickerDialogFragment
import kotlinx.android.synthetic.main.settings_activity.*

/**
 * Created by andreas on 10/07/17.
 */
class SettingsFragment: Fragment() {
    var setting = Setting()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.settings_activity, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting = activity.getSetting()
        consult_rounding.isChecked = setting.consultRounding
        var autoStart = setting.automaticBreakStart
        if(autoStart == 0L)
            lunch_break_time.setText("Choose time")
        else
            lunch_break_time.setText(autoStart.toHourMinute())
        lunch_break_duration.setText(setting.automaticBreak.toHourMinute())
        lunch_break_duration.setOnClickListener {
            var tpdf = TimePickerDialogFragment()
            tpdf.setTargetFragment(this, 0)
            tpdf.show(fragmentManager, "sometag")
        }
        lunch_break_time.setOnClickListener {
            var tpdf = TimePickerDialogFragment()
            tpdf.setTargetFragment(this, 1)
            tpdf.show(fragmentManager, "sometag")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            0 -> {
                var time = TimePickerDialogFragment.fromIntent(data)
                lunch_break_duration.setText(time.toLong().toHourMinute())
                setting.automaticBreak = time
            }
            1 -> {
                var time = TimePickerDialogFragment.fromIntent(data)
                lunch_break_time.setText(time.toLong().toHourMinute())
                setting.automaticBreakStart = time
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        activity.getBus().register(this)
    }

    override fun onPause() {
        super.onPause()
        setting.consultRounding = consult_rounding.isChecked
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("settings").setValue(setting)
        activity.setSetting(setting)
        activity.getBus().post(EventSettingsChanged(setting))
        activity.getBus().unregister(this)
    }
}