package com.tokko.cameandwentv3.log

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.model.Duration
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.Project
import com.tokko.cameandwentv3.settings.SettingsActivity
import kotlinx.android.synthetic.main.log_list_fragment.*
import org.joda.time.DateTime
import java.util.*


/**
 * Created by andre on 1/07/2017.
 */
class LogListFragment: Fragment() {
    var adapter: LogAdapter? = null
    var listener: ValueEventListener? = null
    var dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries")
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
                var logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()
                if(logEntries != null){
                    //cleaning data
                    val (_, toRemove) = LogCleaner().clean(logEntries.toList())
                    if(toRemove.isNotEmpty()){
                        dbRef.removeEventListener(listener)

                        toRemove.forEach { dbRef.child(it.id).removeValue() }

                        dbRef.addValueEventListener(listener)
                        return
                    }
                    logEntries = logEntries.toList().sortedBy { it.timestamp }

                    //constructing durations
                    val durations = logEntries.groupBy { DateTime(it.timestamp).withTimeAtStartOfDay().millis }
                            .map { Duration(it.value, SettingsActivity.getConsultRounding(activity), SettingsActivity.getAutomaticBreakDuration(activity)) }.sortedBy {it.date }
                    adapter!!.clear()
                    adapter!!.addAll(durations)
                    adapter!!.notifyDataSetChanged()
                    val entered = durations.toList().last().logs.toList().sortedBy { x -> x.timestamp }.last().entered
                    clock_button!!.isChecked = entered
                    loglist.expandGroup(adapter?.groupCount!!.minus(1))
                }
                else{
                    adapter!!.clear()
                    adapter!!.notifyDataSetInvalidated()
                    list_empty!!.visibility = View.GONE
                    clock_button.isChecked = false
                }
                clock_button!!.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError?) {}
        }
        clock_button.setOnClickListener { _ ->
            if(clock_button.isChecked) {
                fragmentManager.beginTransaction().addToBackStack("someothertag").replace(android.R.id.content, LogEditFragment.newInstance(null)).commit()
            }
            else{
                val item = adapter!!.getGroup(adapter!!.groupCount - 1).logs.toList().sortedBy { x -> x.timestamp }.last()
                clockin(item.projectId, item.projectTitle, false)
            }
        }
        loglist.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            fragmentManager.beginTransaction().addToBackStack("someothertag").replace(android.R.id.content, LogEditFragment.newInstance(adapter!!.getChild(groupPosition, childPosition))).commit()
            true
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