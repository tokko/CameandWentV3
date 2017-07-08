package com.tokko.cameandwentv3.log

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.Project
import kotlinx.android.synthetic.main.log_edit_fragment.*

/**
 * Created by andre on 8/07/2017.
 */
class LogEditFragment: Fragment() {
    companion object {
        val ARG_LOG_ENTRY = "ARG_LOGENTRY"

        fun newInstance(logEntry: LogEntry?): LogEditFragment{
            val b = Bundle()
            b.putSerializable(ARG_LOG_ENTRY, logEntry)
            val f = LogEditFragment()
            f.arguments = b
            return f
        }
    }
    var adapter: ArrayAdapter<Project>? = null
    var logEntry: LogEntry? = null
    var dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("projects")
    var listener: ValueEventListener = object: ValueEventListener{
        override fun onCancelled(p0: DatabaseError?) {}
        override fun onDataChange(p0: DataSnapshot?) {
            val projects = p0?.getValue(object: GenericTypeIndicator<HashMap<@JvmSuppressWildcards String, @JvmSuppressWildcards Project>>(){ })?.values
            if(projects != null){
                adapter = ArrayAdapter<Project>(activity, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, projects.toList())
                project_picker.adapter = adapter
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEntry = (savedInstanceState ?: arguments)?.getSerializable(ARG_LOG_ENTRY) as? LogEntry ?: LogEntry()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(ARG_LOG_ENTRY, logEntry)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.log_edit_fragment, null ,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindData()
    }

    override fun onStart() {
        super.onStart()
        dbRef.addValueEventListener(listener)

    }

    override fun onStop() {
        super.onStop()
        dbRef.removeEventListener(listener)
    }

    private fun bindData() {
    }
}