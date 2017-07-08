package com.tokko.cameandwentv3

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.otto.Bus
import com.tokko.cameandwentv3.model.LogEntry
import org.joda.time.MutableDateTime
import java.util.HashMap

/**
 * Created by andre on 29/06/2017.
 */
class MyApplication : Application() {
    var bus : Bus = Bus()

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                val logEntries = p0?.getValue(object : GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.toList()
                if(logEntries != null){
                    val am = getSystemService(AudioManager::class.java)
                    if(logEntries.last().entered){
                        val currentSoundMode = am.ringerMode
                        getSharedPreferences("sound", Context.MODE_PRIVATE).edit().putInt("ringermode", currentSoundMode).apply()
                        am.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                    }
                    else{
                        am.ringerMode = getSharedPreferences("sound", Context.MODE_PRIVATE).getInt("ringermode", AudioManager.RINGER_MODE_NORMAL)
                    }
                }
            }
        })
    }

    private fun mockData() {
        val dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val logEntries = p0?.getValue(object : GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.toList()

                logEntries?.forEach { x -> dbRef.child(x.id).removeValue() }
                val mutableDateTime = MutableDateTime(System.currentTimeMillis())
                mutableDateTime.addMonths(-8)
                while (mutableDateTime.millis <= System.currentTimeMillis()) {
                    mutableDateTime.setTime(8, 0, 0, 0)
                    var log = LogEntry(mutableDateTime.millis, true, "someproject", "someproject")
                    dbRef.child(log.id).setValue(log)
                    mutableDateTime.setTime(12, 0, 0, 0)
                    log = LogEntry(mutableDateTime.millis, false, "someproject", "someproject")
                    dbRef.child(log.id).setValue(log)
                    log = LogEntry(mutableDateTime.millis, true, "someproject2", "someproject2")
                    dbRef.child(log.id).setValue(log)
                    mutableDateTime.setTime(17, 0, 0, 0)
                    log = LogEntry(mutableDateTime.millis, false, "someproject2", "someproject2")
                    dbRef.child(log.id).setValue(log)
                    mutableDateTime.addDays(1)
                }
            }
        })
    }
}