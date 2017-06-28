package com.tokko.cameandwentv3.projects

import android.R
import android.app.ListFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.model.Project
import kotlinx.android.synthetic.main.project_fragment.*

class ProjectFragment : ListFragment() {
    var adapter : ArrayAdapter<Project>? = null
    var myRef : DatabaseReference? = null
    var listener : ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var database = FirebaseDatabase.getInstance()
        myRef = database.getReference(uid+"/projects")

       listener = object : ValueEventListener {
           override fun onDataChange(p0: DataSnapshot?) {
               var projects = p0?.getValue(object: GenericTypeIndicator<HashMap<String, Project>>(){ })
               if(projects != null) {
                   var values = projects as HashMap<String, Project>
                   var valuesList = values.values.toList()
                   adapter = ArrayAdapter<Project>(activity, R.layout.simple_list_item_1, R.id.text1, valuesList)
                   listAdapter = adapter
                   adapter!!.notifyDataSetChanged()
               }
           }

           override fun onCancelled(p0: DatabaseError?) {
               Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
           }
       }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(com.tokko.cameandwentv3.R.layout.project_fragment, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_project.setOnClickListener { fragmentManager.beginTransaction().replace(android.R.id.content, ProjectEditFragment.newInstance(null)).addToBackStack("somename").commit() }
    }

    override fun onStart() {
        super.onStart()
        myRef?.addValueEventListener(listener)
    }

    override fun onStop() {
        super.onStop()
        myRef?.removeEventListener(listener)
    }
}