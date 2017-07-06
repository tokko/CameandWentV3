package com.tokko.cameandwentv3.geofence

import android.Manifest
import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.Project
import com.tokko.cameandwentv3.util.PermissionActivity
import java.util.stream.Collectors


/**
 * Created by andreas on 4/07/17.
 */
class GeofenceService : IntentService("GeofenceService") {


    companion object {
        val ACTION_REGISTER = "com.tokko.cameandwentv3.geofenceservice.ACTION_REGISTER"
        val ACTION_REGISTER_PAYLOAD_DELIVERED = "com.tokko.cameandwentv3.geofenceservice.ACTION_REGISTER_PAYLOAD_DELIVERED"
        val EXTRA_PROJECTS = "com.tokko.cameandwentv3.geofenceservice.EXTRA_PROJECTS"
        fun initGeofences(context: Context){
            context.startService(Intent(context, GeofenceService::class.java).setAction(ACTION_REGISTER))
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if(intent?.action.equals(ACTION_REGISTER)) {
            FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("projects").
                    addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            val projects = p0?.getValue(object : GenericTypeIndicator<HashMap<@JvmSuppressWildcards String, @JvmSuppressWildcards Project>>() {})
                            if (projects != null) {
                                val intent = Intent(this@GeofenceService, GeofenceService::class.java).setAction(ACTION_REGISTER_PAYLOAD_DELIVERED)
                                intent.putExtra(EXTRA_PROJECTS, projects)
                                startService(intent)
                            } else {

                            }
                        }
                    })
        }
        else if(intent?.action.equals(ACTION_REGISTER_PAYLOAD_DELIVERED)) {
            val projects = intent?.getSerializableExtra(EXTRA_PROJECTS) as HashMap<String, Project>
            registerGeofences(projects.values)
        }
        else{
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent.hasError()) {

                Log.e("GeofenceService", "Geofence error")
            }
            val geofenceTransition = geofencingEvent.geofenceTransition

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                val triggeringGeofences = geofencingEvent.triggeringGeofences
                val dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries")
                triggeringGeofences.stream().forEach { g ->
                    val logEntry = LogEntry(System.currentTimeMillis(), geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER, g.requestId.split(":")[0], g.requestId.split(":")[2])
                    dbRef.child(logEntry.id).setValue(logEntry)
                }
            }
        }
    }

    private fun registerGeofences(projects: Collection<Project>) {
        unRegisterGeofences()
        val geofences = projects.stream().flatMap { p ->
            p.locations.stream().map { l ->
                Geofence.Builder()
                        .setRequestId(p.id + ":" + l.id + ":" + p.title)
                        .setCircularRegion(l.latitude, l.longitude, 200F)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        //.setLoiteringDelay(1000*60*5)
                        .build()
            }
        }.collect(Collectors.toList())
        val request = GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofences(geofences).build()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val notification = Notification.Builder(this)
                    .setContentTitle("Requires more permissions")
                    .setContentText("This app require permissions to access your location")
                    .setSmallIcon(R.drawable.stat_sys_warning)
                    .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, PermissionActivity::class.java), 0))
                    .setAutoCancel(true)
                    .build()
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(0, notification)
            return
        }
        GeofencingClient(this).addGeofences(request, pendingIntent())

    }

    private fun pendingIntent() = PendingIntent.getService(this, 0, Intent(this, GeofenceService::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

    private fun unRegisterGeofences(){
        GeofencingClient(this).removeGeofences(pendingIntent())
    }
}