package com.tokko.cameandwentv3.notifications

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.model.LogEntry
import java.util.HashMap
import com.tokko.cameandwentv3.log.LogCleaner
import com.tokko.cameandwentv3.model.toHourMinute
import org.joda.time.DateTime


/**
 * Created by andre on 9/07/2017.
 */
class CountdownNotificationService: IntentService("CountdownNotificationService") {
    companion object {
        val ACTION_START = "com.tokko.cameandwentv3.countdownnotificationservices.ACTION_START"
        fun initialize(context: Context){
            context.applicationContext.startService(Intent(context.applicationContext, CountdownNotificationService::class.java).setAction(ACTION_START))
        }
    }
    override fun onHandleIntent(intent: Intent?) {
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                val logEntries = ArrayList(p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>(){ })?.values?.sortedBy { it.timestamp }?.toList())
                val nm = getSystemService(NotificationManager::class.java)
                if(logEntries.isNotEmpty()) {
                    val (cleaned, _) = LogCleaner().clean(logEntries.toList())
                    if(cleaned.last().entered){
                        val today = DateTime(System.currentTimeMillis()).withTimeAtStartOfDay().millis
                        val entriesToday = cleaned.takeLastWhile { it.timestamp > today }
                        val duration = Math.abs(entriesToday.fold(0L, { a, x -> a + if(x.entered) x.timestamp else -x.timestamp}) - System.currentTimeMillis())
                        val max = 8*60*60*1000
                        /*
                        val contentView = RemoteViews(packageName, com.tokko.cameandwentv3.R.layout.notification)
                        contentView.setImageViewResource(com.tokko.cameandwentv3.R.id.image, android.R.drawable.ic_dialog_alert)
                        contentView.setTextViewText(com.tokko.cameandwentv3.R.id.title, "CameAndWentV3")
                        contentView.setTextViewText(com.tokko.cameandwentv3.R.id.text, "Time remaining")
                        */
                        val builder = NotificationCompat.Builder(applicationContext, Notification.CATEGORY_MESSAGE)
                        builder.mContentTitle = ""
                        builder.mContentText = "Time remaining: " + (max - duration).toHourMinute()
                        builder.setSmallIcon(android.R.drawable.ic_dialog_alert)
                       // builder.setCustomBigContentView(contentView)
                        builder.setOngoing(true)
                        builder.setProgress(max, duration.toInt(), false)
                        val notification = builder.build()
                        nm.notify(0, notification)
                    }
                    else{
                        nm.cancel(0)
                    }
                }
            }
        })
    }
}