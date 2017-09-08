package com.tokko.cameandwentv3.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.model.LogEntry
import java.util.HashMap

/**
 * Created by andre on 8/09/2017.
 */
class ClockoutNotification: IntentService(ClockoutNotification::class.java.canonicalName) {
    companion object {
        val ACTION_PUNCH_IN = "com.tokko.cameandwentv3.clockoutnotification.ACTION_PUNH_BACK_IN"
        val ACTION_INIT = "com.tokko.cameandwentv3.clockoutnotification.ACTION_INIT"
        val ACTION_EXPIRE = "com.tokko.cameandwentv3.clockoutnotification.ACTION_EXPIRE"
        fun initialize(context: Context){
            context.startService(Intent(context, ClockoutNotification::class.java).setAction(ACTION_INIT))
        }
    }

    val NOTIFICATION_TIMEOUT = 5 * 60 * 1000

    override fun onHandleIntent(intent: Intent?) {
        if(intent != null) {
            val nm = getSystemService(NotificationManager::class.java)

            when {
                intent.action == ACTION_INIT -> {

                    val builder = NotificationCompat.Builder(applicationContext, Notification.CATEGORY_MESSAGE)
                    builder.mContentTitle = "Left work"
                    builder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    builder.setVibrate(longArrayOf(0, 1000))
                    builder.setLights(Color.MAGENTA, 3000, 3000)
                    builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                    builder.addAction(
                            android.R.drawable.ic_lock_idle_alarm,
                            "Punch back in",
                            PendingIntent.getService(applicationContext,
                                    0,
                                    Intent(applicationContext, ClockoutNotification::class.java).setAction(ClockoutNotification.ACTION_PUNCH_IN),
                                    0))
                    val notification = builder.build()
                    FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").orderByChild("timestamp").limitToLast(1).addValueEventListener(object: ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            val logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()
                            if(logEntries != null) {
                                if (!logEntries.single().entered && System.currentTimeMillis() - logEntries.single().timestamp < NOTIFICATION_TIMEOUT) {
                                    nm.notify(1, notification)
                                    getSystemService(AlarmManager::class.java).set(AlarmManager.RTC, System.currentTimeMillis() + NOTIFICATION_TIMEOUT, PendingIntent.getService(applicationContext, 1, Intent(applicationContext, ClockoutNotification::class.java).setAction(ACTION_EXPIRE), 0))
                                }
                                else
                                    nm.cancel(1)
                            }
                        }
                    })
                }

                intent.action == ACTION_EXPIRE -> nm.cancel(1)
                intent.action == ACTION_PUNCH_IN -> FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        val logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()
                        val logEntry = logEntries?.single()
                        logEntry?.entered = true
                        if(logEntry != null) {
                            FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").child(logEntry.id).removeValue()
                            nm.cancel(1)
                        }
                    }

                })
            }
        }
    }
}