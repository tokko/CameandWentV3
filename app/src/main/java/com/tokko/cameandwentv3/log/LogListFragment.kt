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
import com.tokko.cameandwentv3.settings.getSetting
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
                val logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()
                if(logEntries != null){
                    //cleaning data

                    //constructing durations
                    val durations = logEntries.groupBy { DateTime(it.timestamp).withTimeAtStartOfDay().millis }
                            .map { Duration(it.value, activity.getSetting().consultRounding) }.sortedBy {it.date }

                    val entered = durations.toList().last().logs.toList().sortedBy { x -> x.timestamp }.last().entered
                    val toRemove = durations.flatMap { it.clean() }
                    if(toRemove.isNotEmpty()){
                        dbRef.removeEventListener(listener)

                        toRemove.forEach { dbRef.child(it.id).removeValue() }

                        dbRef.addValueEventListener(listener)
                        return
                    }
                    durations.filter { it.logs.isNotEmpty() }
                    adapter!!.clear()
                    adapter!!.addAll(durations)
                    adapter!!.notifyDataSetChanged()

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
                if(item.timestamp > System.currentTimeMillis()){
                    item.timestamp = System.currentTimeMillis()
                    item.entered = false
                    FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").child(item.id).setValue(item)
                }
                else
                    clock(item.projectId, item.projectTitle, false)
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

    private fun clock(projectId: String, projectTitle: String, action: Boolean){
        val logEntry = LogEntry(System.currentTimeMillis(), action,projectId, projectTitle)
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").child(logEntry.id).setValue(logEntry)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            0 -> {
                val p = data!!.getSerializableExtra("project") as Project
                clock(p.id, p.title, clock_button.isChecked)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}