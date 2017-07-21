package com.tokko.cameandwentv3.automaticbreaks

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.otto.Subscribe
import com.tokko.cameandwentv3.events.EventSettingsChanged
import com.tokko.cameandwentv3.getBus
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.settings.getSetting
import org.joda.time.MutableDateTime
import java.util.HashMap

/**
 * Created by andre on 20/07/2017.
 */
class AutomaticBreakService: Service(), ValueEventListener {
    var latestLog: LogEntry? = null
    override fun onCancelled(p0: DatabaseError?) {
    }

    override fun onDataChange(p0: DataSnapshot?) {
        var logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()
        if(logEntries != null){
            latestLog = logEntries.sortedBy { it.timestamp }.last()
        }
        schedule()
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val ACTION_INIT = "com.tokko.cameandwentv3.automaticbreakservice.ACTION_INIT"
        val ACTION_ON_BREAK_ALARM = "com.tokko.cameandwentv3.automaticbreakservice.ACTION_BREAK_ALARM"
        fun initialize(context: Context){
            context.startService(Intent(context, AutomaticBreakService::class.java).setAction(ACTION_INIT))
        }
    }

    @Subscribe
    fun schedule(event: EventSettingsChanged? = null){
        if(latestLog == null) return
        var setting = event?.setting ?: getSetting()
        var pendingIntent = PendingIntent.getService(applicationContext, 0, Intent(applicationContext, AutomaticBreakService::class.java).setAction(ACTION_ON_BREAK_ALARM), 0)
        var today = MutableDateTime()
        today.setMillisOfDay(setting.automaticBreakStart.toInt())
        var triggerTime = today.millis
        var am = getSystemService(AlarmManager::class.java)
        val entered = latestLog!!.entered
        am.cancel(pendingIntent)
        if(entered){
            am.set(AlarmManager.RTC, triggerTime, pendingIntent)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null){
            if(intent.action!!.equals(ACTION_INIT)){
                getBus().register(this)
                FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").addValueEventListener(this)
            }
            else if(intent.action!!.equals(ACTION_ON_BREAK_ALARM)){
                if(latestLog != null && !latestLog!!.entered){
                    var today = MutableDateTime()
                    today.setMillisOfDay(getSetting().automaticBreakStart.toInt())
                    var triggerTime = today.millis
                    var enterBreakLog = LogEntry(triggerTime, false, latestLog!!.projectId, latestLog!!.projectTitle)
                    val automaticBreakDuration = getSetting().automaticBreak
                    today.millis = triggerTime + automaticBreakDuration
                    var exitBreakLog = LogEntry(today.millis, true, latestLog!!.projectId, latestLog!!.projectTitle)
                    var dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries")
                    dbRef.child(enterBreakLog.id).setValue(enterBreakLog)
                    dbRef.child(exitBreakLog.id).setValue(exitBreakLog)
                }
            }
        }
        return START_STICKY
    }
}