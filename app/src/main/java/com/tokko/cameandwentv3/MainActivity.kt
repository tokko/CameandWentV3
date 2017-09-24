package com.tokko.cameandwentv3

import android.app.AlertDialog
import android.app.Fragment
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.tokko.cameandwentv3.log.LogListFragment
import com.tokko.cameandwentv3.projects.ProjectActivity
import com.tokko.cameandwentv3.settings.SettingsActivity
import com.tokko.cameandwentv3.summary.SummaryActivity

/**
 * Created by andre on 10/06/2017.
 */
class MainActivity : AppCompatActivity() {
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentFragment = if(savedInstanceState != null && savedInstanceState.containsKey("fragment")) fragmentManager.getFragment(savedInstanceState, "fragment") else LogListFragment()
        fragmentManager.beginTransaction().addToBackStack("sometag").replace(android.R.id.content, currentFragment).commit()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val infoDialogBuilder = AlertDialog.Builder(this)
                    .setTitle("Additional permissions required")
                    .setMessage("CameAndWentV3 requires permission to change your Do Not Disturb status to make your phone silent while at work. Toggle the permission in the next view.")
                    .setNegativeButton("I don't  wanna", { dialog, _ -> dialog.dismiss() })
                    .setPositiveButton("Alright then!", { dialog, _ ->
                        val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        startActivityForResult(intent, 1)
                        dialog.dismiss()
                    })
            infoDialogBuilder.show()
            return
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.edit_projevts -> {
                startActivity(Intent(this, ProjectActivity::class.java))
                return true
            }
            R.id.view_summary -> {
                startActivity(Intent(this, SummaryActivity::class.java))
                return true
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragmentManager.putFragment(outState, "fragment", currentFragment)
    }
}

