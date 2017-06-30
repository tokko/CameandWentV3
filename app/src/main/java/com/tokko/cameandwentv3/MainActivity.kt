package com.tokko.cameandwentv3

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tokko.cameandwentv3.log.LogListFragment

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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragmentManager.putFragment(outState, "fragment", currentFragment)
    }
}

