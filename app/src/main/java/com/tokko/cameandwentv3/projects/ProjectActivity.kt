package com.tokko.cameandwentv3.projects

import android.app.Activity
import android.os.Bundle
import com.tokko.cameandwentv3.events.EventEditProject
import com.tokko.cameandwentv3.events.EventEditProjectComplete
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ProjectActivity : Activity() {
    var editFragment: ProjectEditFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState != null && savedInstanceState.containsKey("fragment"))
            editFragment = fragmentManager.getFragment(savedInstanceState, "fragment") as ProjectEditFragment

        fragmentManager.beginTransaction().replace(android.R.id.content, editFragment ?: ProjectFragment()).commit()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onEditProject(event: EventEditProject){
        editFragment = ProjectEditFragment.newInstance(event.project)
        fragmentManager.beginTransaction().replace(android.R.id.content, editFragment).addToBackStack("somename").commit()
    }

    @Subscribe
    fun onEditProjectComplete(@Suppress("UNUSED_PARAMETER") event: EventEditProjectComplete){
        fragmentManager.popBackStack()
        editFragment = null
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragmentManager.putFragment(outState, "fragment", editFragment)
    }
}