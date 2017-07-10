package com.tokko.cameandwentv3.settings

import android.app.Activity
import android.content.Context
import android.os.Bundle

/**
 * Created by andreas on 10/07/17.
 */
class SettingsActivity: Activity() {
    companion object {
        fun getAutomaticBreakDuration(context: Context): Int{
            return context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("automaticbreaks", 30)
        }

        fun setAutomaticBreakDuration(context: Context, duration: Int){
            context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putInt("automaticbreaks", duration).apply()
        }
        fun getConsultRounding(context: Context): Boolean{
            return context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("consultrounding", true)
        }

        fun setConsultRounding(context: Context, rounding: Boolean){
            context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("consultrounding", rounding).apply()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
    }

    override fun onResume() {
        super.onResume()

    }


}