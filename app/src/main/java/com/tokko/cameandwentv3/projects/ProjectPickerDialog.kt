package com.tokko.cameandwentv3.projects

import android.app.Activity
import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.model.Project
import kotlinx.android.synthetic.main.project_fragment.*

/**
 * Created by andre on 4/07/2017.
 */
class ProjectPickerDialog: DialogFragment() {
    var projects: List<Project>? = null
    var listener: ValueEventListener? = null
    var dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("projects")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(com.tokko.cameandwentv3.R.layout.project_fragment, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_project!!.visibility = View.GONE

        list.emptyView = list_empty
        listener = object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                val p = p0?.getValue(object: GenericTypeIndicator<HashMap<@JvmSuppressWildcards String, @JvmSuppressWildcards Project>>(){ })
                p?.entries?.stream()?.map { e -> e.value.id = e.key }
                if(p != null) {
                    projects=p.values.toList()
                    val adapter = object: ArrayAdapter<Project>(activity, android.R.layout.simple_list_item_1, android.R.id.text1, projects){
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                            val v = convertView ?: (activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(android.R.layout.simple_list_item_1, null)
                            v.findViewById<TextView>(android.R.id.text1).text = getItem(position).title
                            return v
                        }
                    }
                    list.adapter = adapter
                    val progressBar = ProgressBar(activity)
                    progressBar.isIndeterminate
                    list.emptyView = progressBar
                    list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                        targetFragment.onActivityResult(0, Activity.RESULT_OK, Intent().putExtra("project", projects!![position]))
                        dismiss()
                    }
                    adapter.notifyDataSetChanged()
                }
                else
                    list_empty!!.visibility = View.GONE
            }

            override fun onCancelled(p0: DatabaseError?) {}
        }
    }

    override fun onStart() {
        super.onStart()
        dbRef.addValueEventListener(listener)
    }

    override fun onStop() {
        super.onStop()
        dbRef.removeEventListener(listener)

    }
}