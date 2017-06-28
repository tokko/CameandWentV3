package com.tokko.cameandwentv3.projects

import android.app.Fragment
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.model.Project
import kotlinx.android.synthetic.main.project_edit_fragment.*

class ProjectEditFragment : Fragment(){
    var project : Project? = null
    var locationAdapter: ArrayAdapter<Location>? = null
    var SSIDAdapter: ArrayAdapter<String>? = null

    companion object Factory {
        fun newInstance(project: Project?): ProjectEditFragment {
            var b = Bundle()
            if (project != null)
                b.putSerializable("project", project)
            var f = ProjectEditFragment()
            f.arguments = b
            return f
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        project = (savedInstanceState ?: arguments)?.getSerializable("project") as? Project ?: Project()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.project_edit_fragment, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable("project", project)
    }

    private fun bindViews() {
        title!!.setText(project!!.title)
        locationAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, android.R.id.text1, project!!.locations)
        SSIDAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, android.R.id.text1, project!!.SSIDs)
        locations.adapter = locationAdapter
        ssids.adapter = SSIDAdapter

        ok.setOnClickListener { FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().currentUser!!.uid+"/projects").push().setValue(project); fragmentManager.popBackStack() }
        cancel.setOnClickListener { fragmentManager.popBackStack() }
    }
}