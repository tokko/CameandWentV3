package com.tokko.cameandwentv3.settings

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.model.Setting
import kotlinx.android.synthetic.main.settings_activity.*

/**
 * Created by andreas on 10/07/17.
 */
class SettingsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.settings_activity, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
        consult_rounding.isChecked = prefs.getBoolean("consultrounding", true)
        automaticbreak.setText(prefs.getInt("automaticbreaks", 30).toString())
    }
    override fun onPause() {
        super.onPause()
        val setting = Setting()
        setting.automaticBreak = automaticbreak.editableText.toString().toInt()
        setting.consultRounding = consult_rounding.isChecked
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("settings").setValue(setting)
    }
}