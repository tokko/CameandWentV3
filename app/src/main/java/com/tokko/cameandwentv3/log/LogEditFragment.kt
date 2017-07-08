package com.tokko.cameandwentv3.log

import android.app.DatePickerDialog
import android.app.Fragment
import android.content.Intent
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
import com.tokko.cameandwentv3.util.DatePickerDialogFragment
import kotlinx.android.synthetic.main.log_edit_fragment.*
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import java.sql.Date
import java.text.SimpleDateFormat

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
        cancel_button.setOnClickListener { fragmentManager.popBackStack() }
        date_picker_button.setOnClickListener { _ ->
            val datePickerFragment = DatePickerDialogFragment()
            datePickerFragment.setTargetFragment(this, 0)
            datePickerFragment.show(fragmentManager, "datepickerfragment")
        }
        bindData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            0 -> {
                val dateTime = DateTime(logEntry!!.timestamp)
                val newDateTime = MutableDateTime(data!!.getLongExtra(DatePickerDialogFragment.RESULT_DATE, 0))
                newDateTime.setTime(dateTime.hourOfDay, dateTime.minuteOfHour, dateTime.secondOfMinute, dateTime.millisOfSecond)
                logEntry!!.timestamp = newDateTime.millis
                date_picker_button.text = SimpleDateFormat("yyyy-MM-dd").format(Date(logEntry!!.timestamp))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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