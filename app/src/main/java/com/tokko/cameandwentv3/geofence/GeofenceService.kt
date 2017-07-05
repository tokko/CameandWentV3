package com.tokko.cameandwentv3.geofence

import android.R
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.IBinder
import android.widget.ArrayAdapter
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.model.Project

/**
 * Created by andreas on 4/07/17.
 */
class GeofenceService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("projects").
                addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        var projects = p0?.getValue(object: GenericTypeIndicator<HashMap<@JvmSuppressWildcards String, @JvmSuppressWildcards Project>>(){ })
                        projects?.entries?.stream()?.map { e -> e.value.id = e.key }
                        if(projects != null) {
                            registerGeofences(projects.values)
                        }
                        else{

                        }
                    }
                })
        return super.onStartCommand(intent, flags, startId)

    }

    private fun registerGeofences(projects: Collection<Project>) {
        //LocationServices.GeofencingApi
    }

    private fun unRegisterGeofences(projects: Collection<Project>){

    }
}