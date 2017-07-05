package com.tokko.cameandwentv3.log

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
    var adapter: ArrayAdapter<LogEntry>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.log_list_fragment, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = object: ArrayAdapter<LogEntry>(activity, android.R.layout.simple_list_item_1, android.R.id.text1){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val v = convertView ?: (activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.log_entry, null)
                val item = getItem(position)
                (v.findViewById(R.id.timestamp) as TextView).text = SimpleDateFormat("HH:mm:ss").format(item?.timestamp) ?: ""
                (v.findViewById(R.id.action) as TextView).text = if(item.entered) "Arrived" else "Departed"
                (v.findViewById(R.id.project_name) as TextView).text = item?.projectTitle
                return v
            }
        }


        loglist.adapter = adapter
        loglist.emptyView = list_empty
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                var logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@JvmSuppressWildcards String, @JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()
                logEntries = logEntries?.sortedBy { c -> c.timestamp }
                adapter!!.clear()
                if(logEntries != null){
                    adapter!!.addAll(logEntries.toList())
                    adapter!!.notifyDataSetChanged()
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
        })
        clock_button.setOnClickListener { _ ->
            if(clock_button.isChecked) {
                val projectPicker = ProjectPickerDialog()
                projectPicker.setTargetFragment(this, 0)
                projectPicker.show(activity.fragmentManager, "some tag")
            }
            else{
                val item = adapter!!.getItem(adapter!!.count - 1)
                clockin(item.projectId, item.projectTitle, false)
            }
        }
    }

    private fun clockin(projectId: String, projectTitle: String, action: Boolean){
        val duration = System.currentTimeMillis()
        val hours = TimeUnit.MILLISECONDS.toHours(duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours))
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
        val durationString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
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