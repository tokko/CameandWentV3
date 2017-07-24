package com.tokko.cameandwentv3.automaticbreaks

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.events.EventSettingsChanged
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.settings.getSetting
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.joda.time.MutableDateTime
import java.util.*

/**
 * Created by andre on 20/07/2017.
 */
class AutomaticBreakService : IntentService("AutomaticBreakService") {
    companion object {
        val ACTION_INIT = "com.tokko.cameandwentv3.automaticbreakservice.ACTION_INIT"
        val ACTION_ON_BREAK_ALARM = "com.tokko.cameandwentv3.automaticbreakservice.ACTION_BREAK_ALARM"
        fun initialize(context: Context){
            context.startService(Intent(context, AutomaticBreakService::class.java).setAction(ACTION_INIT))
        }
    }

    @Subscribe
    fun schedule(event: EventSettingsChanged? = null){

        var setting = event?.setting ?: getSetting()
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                var logEntries = p0?.getValue(object : GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.toList()
                if (logEntries != null) {
                    var pendingIntent = PendingIntent.getService(applicationContext, 0,
                            Intent(applicationContext, AutomaticBreakService::class.java).setAction(ACTION_ON_BREAK_ALARM),
                            0)
                    var today = MutableDateTime()
                    today.millisOfDay = setting.automaticBreakStart.toInt()
                    var triggerTime = today.millis
                    var am = getSystemService(AlarmManager::class.java)
                    val entered = logEntries.sortedBy { it.timestamp }.last().entered
                    am.cancel(pendingIntent)
                    if (triggerTime > System.currentTimeMillis() && entered) {
                        am.set(AlarmManager.RTC, triggerTime, pendingIntent)
                    }
                }
            }
        })
    }

    override fun onHandleIntent(intent: Intent?) {
        if(intent != null){
            if(intent.action!!.equals(ACTION_INIT)){
                EventBus.getDefault().register(this)
                schedule()
            }
            else if(intent.action!!.equals(ACTION_ON_BREAK_ALARM)){
                FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries")
                        .orderByChild(LogEntry::timestamp.name).limitToLast(1)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {}
                            override fun onDataChange(p0: DataSnapshot?) {
                                var logEntries = p0?.getValue(object : GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.toList()
                                if (logEntries != null) {
                                    var latestLog = logEntries.sortedBy { it.timestamp }.last()
                                    if (latestLog != null && latestLog.entered) {
                                        var today = MutableDateTime()
                                        today.millisOfDay = getSetting().automaticBreakStart.toInt()
                                        var triggerTime = today.millis
                                        var enterBreakLog = LogEntry(triggerTime, false, latestLog.projectId, latestLog.projectTitle)
                                        val automaticBreakDuration = getSetting().automaticBreakDuration
                                        today.millis = triggerTime + automaticBreakDuration
                                        var exitBreakLog = LogEntry(today.millis, true, latestLog.projectId, latestLog.projectTitle)
                                        var dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries")
                                        dbRef.child(enterBreakLog.id).setValue(enterBreakLog)
                                        dbRef.child(exitBreakLog.id).setValue(exitBreakLog)
                                    }
                                }
                            }
                        })
            }
        }
    }
}