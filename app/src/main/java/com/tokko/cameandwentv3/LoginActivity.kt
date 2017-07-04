package com.tokko.cameandwentv3

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import com.google.firebase.auth.GoogleAuthProvider


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
                                val user = mAuth.currentUser
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Auth", "signInWithCredential:failure", task.exception)
                                Toast.makeText(this@LoginActivity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                            }

                            // ...
                        }

            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

}
