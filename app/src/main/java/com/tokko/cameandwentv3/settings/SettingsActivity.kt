package com.tokko.cameandwentv3.settings

import android.app.Activity
import android.content.Context
import android.os.Bundle

/**
 * Created by andreas on 10/07/17.
 */
fun Context.getAutomaticBreakDuration(): Long{
    return this.getSharedPreferences("settings", Context.MODE_PRIVATE).getLong("automaticbreaks", 30)
}

fun Context.setAutomaticBreakDuration(duration: Long){
    this.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putLong("automaticbreaks", duration).apply()
}
fun Context.getAutomaticBreakStart(): Long{
    return this.getSharedPreferences("settings", Context.MODE_PRIVATE).getLong("automaticstart", 0)
}

fun Context.setAutomaticBreakStart(duration: Long){
    this.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putLong("automaticstart", duration).apply()
}

fun Context.getConsultRounding(): Boolean{
    return this.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("consultrounding", true)
}

fun Context.setConsultRounding(rounding: Boolean) {
    this.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("consultrounding", rounding).apply()
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