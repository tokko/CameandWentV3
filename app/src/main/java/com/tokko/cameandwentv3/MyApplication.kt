package com.tokko.cameandwentv3

import android.app.Application
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
        val dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries")
        dbRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()

                logEntries?.forEach { x -> dbRef.child(x.id).removeValue() }
                val dt = MutableDateTime(System.currentTimeMillis())
                var log = LogEntry(dt.millis, true, "someid", "someproject")
                dbRef.child(log.id).setValue(log)
                dt.addHours(1)
                log = LogEntry(dt.millis, false, "someid", "someproject")
                dbRef.child(log.id).setValue(log)
                dt.addDays(-1)
                log = LogEntry(dt.millis, true, "someid", "someproject")
                dbRef.child(log.id).setValue(log)
                dt.addHours(1)
                log = LogEntry(dt.millis, false, "someid", "someproject")
                dbRef.child(log.id).setValue(log)
            }
        })

    }
}