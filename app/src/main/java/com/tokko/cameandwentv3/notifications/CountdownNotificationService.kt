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
import android.widget.RemoteViews



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
                val logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()
                val nm = getSystemService(NotificationManager::class.java)
                if(logEntries != null) {
                    if(logEntries.last().entered){

                        val contentView = RemoteViews(packageName, com.tokko.cameandwentv3.R.layout.notification)
                        contentView.setImageViewResource(com.tokko.cameandwentv3.R.id.image, android.R.drawable.ic_dialog_alert)
                        contentView.setTextViewText(com.tokko.cameandwentv3.R.id.title, "Custom notification")
                        contentView.setTextViewText(com.tokko.cameandwentv3.R.id.text, "This is a custom layout")
                        val builder = NotificationCompat.Builder(applicationContext, Notification.CATEGORY_MESSAGE)
                        builder.mContentTitle = "CameAndWentV3"
                        builder.mContentText = "Time remaining"
                        builder.setSmallIcon(android.R.drawable.ic_dialog_alert)
                        builder.setCustomBigContentView(contentView)

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