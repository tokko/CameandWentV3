package com.tokko.cameandwentv3.projects

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import java.util.*
import android.text.TextUtils
import android.location.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tokko.cameandwentv3.model.Project
import java.io.IOException
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver


/**
 * Created by andre on 8/07/2017.
 */
class GeocodingService: IntentService("GeocodingService") {
    companion object {
        val EXTRA_PROJECT_ID = "project id"
        val EXTRA_LONGITUDE = "location id"
        val EXTRA_LATITUDE = "latitude id"
        val EXTRA_RECEIVER = "receiver"
        fun startService(context: Context, projectId: String, latitude: Double, longitude: Double, resultReceiver: ResultReceiver){
            context.applicationContext.startService(Intent(context.applicationContext, GeocodingService::class.java).putExtra(EXTRA_PROJECT_ID, projectId).putExtra(EXTRA_LONGITUDE, longitude).putExtra(EXTRA_LATITUDE, latitude).putExtra(EXTRA_RECEIVER, resultReceiver))
        }
    }

    override fun onHandleIntent(p0: Intent?) {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(
                    p0!!.getDoubleExtra(EXTRA_LATITUDE, 0.0),
                    p0.getDoubleExtra(EXTRA_LONGITUDE, 0.0),
                    // In this sample, get just a single address.
                    1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
        } catch (illegalArgumentException: IllegalArgumentException) {
        }


        // Handle case where no address was found.
        if (addresses == null || addresses.isEmpty()) {
        } else {
            val address = addresses[0]
            //val addressFragments = (0..address.maxAddressLineIndex).mapTo(ArrayList<String>()) { address.getAddressLine(it) }


            val b = Bundle()
            b.putString("address", address.getAddressLine(0))
            val resultReceiver = p0!!.getParcelableExtra<ResultReceiver>(EXTRA_RECEIVER)
            resultReceiver.send(0, b)
        }
    }
}