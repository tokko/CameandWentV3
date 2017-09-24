package com.tokko.cameandwentv3.wifi

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.tokko.cameandwentv3.getDbRef
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.Project

/**
 * Created by andre on 23/09/2017.
 */
fun Context.attemptClockout() {
    this.startService(Intent(this, WifiService::class.java).setAction(WifiService.ACTION_ATTEMPT_CLOCKOUT))
}

class WifiService : Service() {

    companion object {
        val ACTION_ATTEMPT_CLOCKOUT = "com.tokko.cameandwentv3.wifi.wifiservice.ACTION_ATTEMPT_CLOCKOUT"
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun asLocation(latitude: Double, longitude: Double): Location {
        val l = Location("")
        l.longitude = longitude
        l.latitude = latitude
        return l
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getDbRef().child("logentry").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p1: DatabaseError?) {}
            override fun onDataChange(p1: DataSnapshot?) {
                val logEntries = p1?.getValue(object : GenericTypeIndicator<java.util.HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.toList()
                if (logEntries != null) {
                    val logEntry = logEntries.sortedBy { it.timestamp }.last()
                    if (!logEntry.entered) stopSelf()

                }
            }
        })
        getLocation()
        return START_STICKY
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.powerRequirement = Criteria.POWER_HIGH
        mLocationManager.requestSingleUpdate(criteria, object : LocationListener {
            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            }

            override fun onProviderEnabled(p0: String?) {
            }

            override fun onProviderDisabled(p0: String?) {
            }

            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    getDbRef().child("logentry").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p1: DatabaseError?) {}
                        override fun onDataChange(p1: DataSnapshot?) {
                            val logEntries = p1?.getValue(object : GenericTypeIndicator<java.util.HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.toList()
                            if (logEntries != null) {
                                val logEntry = logEntries.sortedBy { it.timestamp }.last()
                                if (!logEntry.entered) stopSelf()
                                getDbRef().child("projects").child(logEntry.projectId).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError?) {}

                                    override fun onDataChange(p0: DataSnapshot?) {
                                        val project = p0?.getValue(object : GenericTypeIndicator<java.util.HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards Project>>() {})?.values?.single()
                                        if (project != null) {
                                            val distances = project.locations.map { asLocation(it.latitude, it.longitude) }.map { it.distanceTo(location) }
                                            val limit = 100 //TODO("Distance as setting")
                                            if (distances.any { it < limit }) stopSelf()

                                            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                                            val info = wifiManager.connectionInfo
                                            val ssid = info.ssid.replace("\"", "")
                                            if (project.SSIDs.any { ssid == it }) stopSelf()
                                            val newEntry = LogEntry(System.currentTimeMillis(), false, logEntry.projectId, logEntry.projectTitle)
                                            getDbRef().child(newEntry.id).setValue(newEntry)
                                        }
                                    }
                                })
                            }
                        }
                    })
                }
            }
        }, Looper.getMainLooper())
    }
}