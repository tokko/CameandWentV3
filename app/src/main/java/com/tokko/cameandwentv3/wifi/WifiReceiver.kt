package com.tokko.cameandwentv3.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.Project


/**
 * Created by andreas on 6/07/17.
 */
class WifiReceiver : BroadcastReceiver() {
    val noWifiSSID = "<unknown ssid>"
    override fun onReceive(context: Context?, intent: Intent?) {
        val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        val ssid = info.ssid.replace("\"", "")
        val dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid)
        dbRef.child("projects")
                .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                val projects = p0?.getValue(object : GenericTypeIndicator<HashMap<@JvmSuppressWildcards String, @JvmSuppressWildcards Project>>() {})?.values
                if (projects != null) {
                    if(ssid == noWifiSSID){
                        clockoutLatestProject()
                    }
                    else{
                        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentry").addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onCancelled(p1: DatabaseError?) {}
                            override fun onDataChange(p1: DataSnapshot?) {
                                val logEntries = p1?.getValue(object : GenericTypeIndicator<java.util.HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.toList()
                                if(logEntries != null && logEntries.sortedBy { it.timestamp }.last().entered) return
                                val project = projects.singleOrNull { p -> p.SSIDs.any { s -> s == ssid } }
                                if(project != null) {
                                    val logEntry = LogEntry(System.currentTimeMillis(), true, project.id, project.title)
                                    FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").child(logEntry.id)
                                            .setValue(logEntry)
                                }
                            }
                        })

                    }
                } else {

                }
            }
            override fun onCancelled(p0: DatabaseError?) {
            }
        })
    }

    private fun clockoutLatestProject() {
        val dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries")
        dbRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                val entries = p0?.getValue(object : GenericTypeIndicator<HashMap<@JvmSuppressWildcards String, @JvmSuppressWildcards LogEntry>>() {})?.values
                val latestEntry = entries?.sortedByDescending { x -> x.timestamp }?.first()
                if(latestEntry != null){
                    val newEntry = LogEntry(System.currentTimeMillis(), false, latestEntry.projectId, latestEntry.projectTitle)
                    dbRef.child(newEntry.id).setValue(newEntry)
                }

            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}