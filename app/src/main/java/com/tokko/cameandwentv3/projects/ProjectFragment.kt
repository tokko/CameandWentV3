package com.tokko.cameandwentv3.projects

import android.app.ListFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.*
import com.tokko.cameandwentv3.events.EventEditProject
import com.tokko.cameandwentv3.getDbRef
import com.tokko.cameandwentv3.model.Project
import kotlinx.android.synthetic.main.project_fragment.*
import org.greenrobot.eventbus.EventBus

class ProjectFragment : ListFragment() {
    var adapter : ArrayAdapter<Project>? = null
    var myRef : DatabaseReference? = null
    var listener : ValueEventListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myRef = getDbRef().child("projects")
        listener = object : ValueEventListener {
           override fun onDataChange(p0: DataSnapshot?) {
               val projects = p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards Project>>(){ })
               if(projects != null) {
                   adapter = ArrayAdapter<Project>(activity, android.R.layout.simple_list_item_1, android.R.id.text1, projects.values.toList())
                   listAdapter = adapter
                   adapter!!.notifyDataSetChanged()
               }
               else{
                   list_empty!!.visibility = View.GONE
               }
               add_project.visibility = View.VISIBLE
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
        add_project.setOnClickListener { EventBus.getDefault().post(EventEditProject(null)) }
        list.setOnItemClickListener({ _, _, position, _ -> EventBus.getDefault()!!.post(EventEditProject(adapter!!.getItem(position))) })
        list.emptyView = list_empty
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