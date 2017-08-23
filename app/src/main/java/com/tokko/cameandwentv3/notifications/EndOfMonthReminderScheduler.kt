package com.tokko.cameandwentv3.notifications

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.joda.time.DateTime
import org.joda.time.MutableDateTime

/**
 * Created by andreas on 23/08/17.
 */
class EndOfMonthReminderScheduler : IntentService("EndOfMonthReminderScheduler") {

    companion object {
        var ACTION_SCHEDULE = "com.tokko.cameandwentv3.ACTION_SCHEDULE"
        var ACTION_NOTIFY = "com.tokko.cameandwentv3.ACTION_NOTIFY"

        fun init(context: Context) {
            context.applicationContext.startService(Intent(context.applicationContext, EndOfMonthReminderScheduler::class.java).setAction(ACTION_SCHEDULE))
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val pi = PendingIntent.getService(applicationContext, 0, Intent(applicationContext, EndOfMonthReminderScheduler::class.java).setAction(ACTION_NOTIFY), 0)
            val am = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (intent.action!! == ACTION_SCHEDULE) {
                am.cancel(pi)
            }
        }
    }

    fun getEndOfMonth(start: DateTime): DateTime {
        return DateTime()
    }


}