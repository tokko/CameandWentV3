package com.tokko.cameandwentv3

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tokko.cameandwentv3.log.LogListFragment
import android.view.Menu
import android.view.MenuItem
import com.tokko.cameandwentv3.projects.ProjectActivity
import com.tokko.cameandwentv3.summary.SummaryActivity

/**
 * Created by andre on 10/06/2017.
 */
class MainActivity : AppCompatActivity() {
    var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentFragment = if(savedInstanceState != null && savedInstanceState.containsKey("fragment")) fragmentManager.getFragment(savedInstanceState, "fragment") else LogListFragment()
        fragmentManager.beginTransaction().replace(android.R.id.content, currentFragment).commit()
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
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragmentManager.putFragment(outState, "fragment", currentFragment)
    }
}

