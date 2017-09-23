package com.tokko.cameandwentv3

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.tokko.cameandwentv3.automaticbreaks.AutomaticBreakService
import com.tokko.cameandwentv3.geofence.GeofenceService
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.Setting
import com.tokko.cameandwentv3.notifications.ClockoutNotification
import com.tokko.cameandwentv3.notifications.CountdownNotificationService
import com.tokko.cameandwentv3.settings.setSetting
import com.tokko.cameandwentv3.wifi.WifiReceiver
import java.util.*


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : FragmentActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
    }

    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
        var client = "552248423889-pjailnadigjg55n0ft2qc5scbq6pa1pf.apps.googleusercontent.com"

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(client)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build()

        signIn()
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, 0)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 0) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Auth", "signInWithCredential:success")
                                initPostLogin()
                                if (intent != null && !intent.getBooleanExtra("skipresume", false))
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Auth", "signInWithCredential:failure", task.exception)
                                Toast.makeText(this@LoginActivity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }

            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
                setupSoundToggling()
        }
    }
    private fun setupSoundToggling() {

        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").orderByChild("timestamp").limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                val logEntries = p0?.getValue(object : GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>() {})?.values?.toList()
                if (logEntries != null) {
                    val am = getSystemService(AudioManager::class.java)
                    if (logEntries.last().entered) {
                        val currentSoundMode = am.ringerMode
                        getSharedPreferences("sound", Context.MODE_PRIVATE).edit().putInt("ringermode", currentSoundMode).apply()
                        am.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                    } else {
                        am.ringerMode = getSharedPreferences("sound", Context.MODE_PRIVATE).getInt("ringermode", AudioManager.RINGER_MODE_NORMAL)
                    }
                }
            }
        })
    }

    private fun initPostLogin() {
        GeofenceService.initGeofences(applicationContext)
        applicationContext.registerReceiver(WifiReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        setupSoundToggling()
        CountdownNotificationService.initialize(applicationContext)
        setupSettingSync()
        AutomaticBreakService.initialize(applicationContext)
        ClockoutNotification.initialize(applicationContext)
    }

    private fun setupSettingSync() {
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("settings").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                val setting = p0?.getValue(Setting::class.java)
                if(setting != null){
                    setSetting(setting)
                }
            }
        })
    }

}
