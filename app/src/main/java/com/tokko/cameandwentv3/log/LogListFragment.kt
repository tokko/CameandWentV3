package com.tokko.cameandwentv3.log

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.Project
import com.tokko.cameandwentv3.projects.ProjectPickerDialog
import kotlinx.android.synthetic.main.log_entry.*
import kotlinx.android.synthetic.main.log_list_fragment.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit



/**
 * Created by andre on 1/07/2017.
 */
class LogListFragment: Fragment() {
    var adapter: LogAdapter? = null
    var listener: ValueEventListener? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.log_list_fragment, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = LogAdapter(activity)
        loglist.setAdapter(adapter)

        loglist.emptyView = list_empty
        listener = object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                var logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@JvmSuppressWildcards String, @JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()
                logEntries = logEntries?.sortedBy { c -> c.timestamp }
                adapter!!.clear()
                if(logEntries != null){
                    adapter!!.addAll(logEntries.toList())
                    val entered = logEntries.toList()[logEntries.count() - 1].entered
                    clock_button!!.isChecked = entered
                }
                else{
                    list_empty!!.visibility = View.GONE
                    clock_button.isChecked = false
                }
                clock_button!!.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError?) {}
        }
        clock_button.setOnClickListener { _ ->
            if(clock_button.isChecked) {
                val projectPicker = ProjectPickerDialog()
                projectPicker.setTargetFragment(this, 0)
                projectPicker.show(activity.fragmentManager, "some tag")
            }
            else{
             //   val item = adapter!!.getItem(adapter!!.count - 1)
             //   clockin(item.projectId, item.projectTitle, false)
                //TODO(reimplement with expandable)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").addValueEventListener(listener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseDatabase.getInstance().reference.removeEventListener(listener)

    }

    private fun clockin(projectId: String, projectTitle: String, action: Boolean){
        val logEntry = LogEntry(System.currentTimeMillis(), action,projectId, projectTitle)
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").child(logEntry.id).setValue(logEntry)
      //  adapter!!.add(LogEntry(System.currentTimeMillis(), action,projectId, projectTitle))
      // adapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            0 -> {
                val p = data!!.getSerializableExtra("project") as Project
                clockin(p.id, p.title, clock_button.isChecked)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}