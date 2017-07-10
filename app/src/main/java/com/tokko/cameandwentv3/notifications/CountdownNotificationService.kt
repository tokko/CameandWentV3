package com.tokko.cameandwentv3.notifications

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.log.LogCleaner
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.toHourMinute
import org.joda.time.DateTime
import java.util.*


/**
 * Created by andre on 9/07/2017.
 */
class CountdownNotificationService: Service(){
    var tickReceiver = TimeTickReceiver()
    var screenReceiver = ScreenReceiver()
    var entriesToday: List<LogEntry>? = null

    override fun onBind(p0: Intent?): IBinder {
        TODO("Binder not implemented")
    }

    companion object {
        val ACTION_START = "com.tokko.cameandwentv3.countdownnotificationservices.ACTION_START"
        val ACTION_UPDATE = "com.tokko.cameandwentv3.countdownnotificationservices.ACTION_UPDATE"
        fun initialize(context: Context){
            context.applicationContext.startService(Intent(context.applicationContext, CountdownNotificationService::class.java).setAction(ACTION_START))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nm = getSystemService(NotificationManager::class.java)
        if(intent?.action == ACTION_START) {
            FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {}
                override fun onDataChange(p0: DataSnapshot?) {
                    val logEntries = p0?.getValue(object : GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.sortedBy { it.timestamp }?.toList()
                    if (logEntries != null) {
                        val (cleaned, _) = LogCleaner().clean(logEntries.toList())
                        if (cleaned.last().entered) {
                            val today = DateTime(System.currentTimeMillis()).withTimeAtStartOfDay().millis
                            entriesToday = cleaned.takeLastWhile { it.timestamp > today }
                            updateNotification(nm)
                            registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
                            val intentFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)
                            intentFilter.addAction(Intent.ACTION_SCREEN_ON)
                            registerReceiver(screenReceiver, intentFilter)
                            }
                        }
                    else {
                        unregisterReceiver(tickReceiver)
                        unregisterReceiver(screenReceiver)
                        nm.cancel(0)
                    }
                }
            })
        }
        else if(intent?.action == ACTION_UPDATE){
            updateNotification(nm)
        }
        else if(intent?.action == Intent.ACTION_SCREEN_OFF){
            unregisterReceiver(tickReceiver)
        }
        else if(intent?.action == Intent.ACTION_SCREEN_ON){
            registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
            updateNotification(nm)
        }
        return Service.START_STICKY
    }

    private fun updateNotification(nmp: NotificationManager? = null) {
        val nm = nmp ?: getSystemService(NotificationManager::class.java)
        val duration = Math.abs(entriesToday?.fold(0L, { a, x -> a + if (x.entered) x.timestamp else -x.timestamp })?.minus(System.currentTimeMillis()) ?: 0L)
        val max = 8 * 60 * 60 * 1000
        val builder = NotificationCompat.Builder(applicationContext, Notification.CATEGORY_MESSAGE)
        builder.mContentTitle = ""
        builder.mContentText = "Time remaining: " + (max - duration).toHourMinute()
        builder.setSmallIcon(R.drawable.ic_dialog_alert)
        builder.setOngoing(true)
        builder.setProgress(max, duration.toInt(), false)
        val notification = builder.build()
        nm.notify(0, notification)
    }


}