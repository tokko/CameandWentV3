package com.tokko.cameandwentv3.settings

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.gson.Gson
import com.tokko.cameandwentv3.model.Setting

/**
 * Created by andreas on 10/07/17.
 */
fun Context.getSetting(): Setting {
    val string = this.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("settings", "")
    if(string.isEmpty()){
        var setting = Setting()
        setting.consultRounding = true
        setting.automaticBreakDuration = 30 * 60 * 1000
        setting.automaticBreakStart = 11*60*60*1000 + 30*60*1000
        return setting
    }
    return Gson().fromJson(string, Setting::class.java)
}

fun Context.setSetting(setting: Setting){
    this.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putString("settings", Gson().toJson(setting)).apply()
}
class SettingsActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
    }

    override fun onResume() {
        super.onResume()

    }


}