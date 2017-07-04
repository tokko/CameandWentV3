package com.tokko.cameandwentv3.projects

import android.R
import android.app.Activity
import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.Gravity
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(android.R.layout.list_content, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("projects").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                val p = p0?.getValue(object: GenericTypeIndicator<HashMap<@JvmSuppressWildcards String, @JvmSuppressWildcards Project>>(){ })
                p?.entries?.stream()?.map { e -> e.value.id = e.key }
                if(p != null) {
                    projects=p.values.toList()
                    val adapter = object: ArrayAdapter<Project>(activity, R.layout.simple_list_item_1, R.id.text1, projects){
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                            val v = convertView ?: (activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.simple_list_item_1, null)
                            (v.findViewById(android.R.id.text1) as TextView).text = getItem(position).title
                            return v
                        }
                    }
                    val listView = getView().findViewById(android.R.id.list) as ListView
                    listView.adapter = adapter
                    val progressBar = ProgressBar(activity)
                    progressBar.isIndeterminate
                    var params = ViewGroup.LayoutParams(progressBar.layoutParams) as LinearLayout.LayoutParams
                    params.gravity = Gravity.CENTER
                    listView.emptyView = progressBar
                            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                        targetFragment.onActivityResult(1, Activity.RESULT_OK, Intent().putExtra("project", projects!![position]))
                    }
                    adapter.notifyDataSetChanged()
                }
                else
                    list_empty!!.visibility = View.GONE
                add_project!!.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError?) {


            }
        })
    }
}