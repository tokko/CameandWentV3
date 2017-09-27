package com.tokko.cameandwentv3.notifications

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.MainActivity
import com.tokko.cameandwentv3.model.Duration
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.toHourMinute
import com.tokko.cameandwentv3.settings.getSetting
import org.joda.time.DateTime
import java.util.*


/**
 * Created by andre on 9/07/2017.
 */
class CountdownNotificationService: Service(){
    var tickReceiver: TimeTickReceiver? = null
    var screenReceiver: ScreenReceiver? = null
    var entriesToday: List<LogEntry>? = null

    override fun onBind(p0: Intent?): IBinder {
        TODO("Binder not implemented")
    }

    companion object {
        val ACTION_START = "com.tokko.cameandwentv3.countdownnotificationservices.ACTION_START"
        val ACTION_UPDATE = "com.tokko.cameandwentv3.countdownnotificationservices.ACTION_UPDATE"
        val ACTION_PUNCH_OUT = "com.tokko.cameandwentv3.countdownnotificationservices.ACTION_PUNCH_OUT"
        fun initialize(context: Context){
            context.applicationContext.startService(Intent(context.applicationContext, CountdownNotificationService::class.java).setAction(ACTION_START))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nm = getSystemService(NotificationManager::class.java)
        if(intent?.action == ACTION_START) {
            FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("logentries")
                    .orderByChild(LogEntry::timestamp.name)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {}
                        override fun onDataChange(p0: DataSnapshot?) {
                            val logEntries = p0?.getValue(object : GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.sortedBy { it.timestamp }?.toList()
                            if (logEntries != null) {
                                if (logEntries.last().entered) {
                                    val today = DateTime(System.currentTimeMillis()).withTimeAtStartOfDay().millis
                                    entriesToday = logEntries.takeLastWhile { it.timestamp >= today }
                                    updateNotification(nm)
                                    registerScreenReceiver()
                                    registerTickReceiver()
                                } else {
                                    unRegisterTickReceiver()
                                    unregisterScreenReceiver()
                                    nm.cancel(0)
                                }
                            } else {
                                unRegisterTickReceiver()
                                unregisterScreenReceiver()
                                nm.cancel(0)
                            }
                        }
                    })
        }
        else if(intent?.action == ACTION_UPDATE){
            updateNotification(nm)
        }
        else if(intent?.action == Intent.ACTION_SCREEN_OFF){
            unRegisterTickReceiver()
        }
        else if(intent?.action == Intent.ACTION_SCREEN_ON){
            registerTickReceiver()
            updateNotification(nm)
        }
        else if(intent?.action == ACTION_PUNCH_OUT){
            val entry = LogEntry(System.currentTimeMillis(), false, entriesToday?.last()?.projectId, entriesToday?.last()?.projectTitle)
            FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").child(entry.id).setValue(entry)
        }
        return Service.START_STICKY
    }

    private fun unregisterScreenReceiver() {
        if(screenReceiver != null)
            unregisterReceiver(screenReceiver)
        screenReceiver = null
    }

    private fun registerScreenReceiver(){
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        if(screenReceiver == null)
            screenReceiver = ScreenReceiver()
        registerReceiver(screenReceiver, intentFilter)
    }

    private fun unRegisterTickReceiver(){
        if(tickReceiver != null)
            unregisterReceiver(tickReceiver)
        tickReceiver = null
    }
    private fun registerTickReceiver(){
        if(tickReceiver == null)
            tickReceiver = TimeTickReceiver()
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }
    private fun updateNotification(nmp: NotificationManager? = null) {
        val nm = nmp ?: getSystemService(NotificationManager::class.java)
        //var duration = this.entriesToday!!.fold(0L) { a, x -> a + if (x.entered) x.timestamp else -x.timestamp }
        val dur = Duration(entriesToday!!, false, true)
        var duration = dur.durationLong

        duration = Math.abs(duration)
        //val duration = Math.abs(entriesToday!!.fold(-Math.max(System.currentTimeMillis(), entriesToday!!.last().timestamp), { a, x -> a + if (x.entered) x.timestamp else -x.timestamp }))
        //  if(entriesToday!!.last().timestamp < System.currentTimeMillis())
        //      duration = duration?.minus(System.currentTimeMillis()) ?: 0L
        val max = 8 * 60 * 60 * 1000// + getSetting().automaticBreakDuration
        val builder = NotificationCompat.Builder(applicationContext, Notification.CATEGORY_MESSAGE)
        builder.mContentTitle = ""
        builder.mContentText = "Time remaining: " + (max - duration).toHourMinute()
        builder.setSmallIcon(R.drawable.ic_lock_idle_alarm)
        builder.setOngoing(true)
        builder.setProgress(max, duration.toInt(), false)
        builder.setContentIntent(PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, MainActivity::class.java), 0))

        builder.addAction(
                R.drawable.ic_lock_idle_alarm,
                "Punch out",
                PendingIntent.getService(applicationContext,
                        0,
                        Intent(applicationContext, CountdownNotificationService::class.java).setAction(ACTION_PUNCH_OUT),
                        0))
        val notification = builder.build()
        nm.notify(0, notification)
    }
}
