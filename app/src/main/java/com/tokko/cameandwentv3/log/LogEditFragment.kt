package com.tokko.cameandwentv3.log

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
import com.tokko.cameandwentv3.util.TimePickerDialogFragment
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
    var endTimestamp: Long = 0
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
        start_time_button.setOnClickListener { _ ->
            val timePickerDialog = TimePickerDialogFragment()
            timePickerDialog.setTargetFragment(this, 1)
            timePickerDialog.show(fragmentManager, "timepickerfragment")
        }
        end_time_button.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked){
                val timePickerDialog = TimePickerDialogFragment()
                timePickerDialog.setTargetFragment(this, 2)
                timePickerDialog.show(fragmentManager, "timepickerfragment")
            }
        }
        bindData()
        ok_button.setOnClickListener { _ ->
            val project = adapter!!.getItem(project_picker.selectedItemPosition)
            logEntry!!.id = project.id
            logEntry!!.projectTitle = project.title
            logEntry!!.entered = true
            val dbRef = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries")
            dbRef.child(logEntry!!.id).setValue(logEntry)
            if(endTimestamp != 0L){
                val endLog = LogEntry(DateTime(logEntry!!.timestamp).withMillisOfDay(endTimestamp.toInt()).millis, false, logEntry!!.projectId, logEntry!!.projectTitle)
                dbRef.child(endLog.id).setValue(endLog)
            }
            fragmentManager.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            0 -> {
                setDate(data!!.getLongExtra(DatePickerDialogFragment.RESULT_DATE, 0))
            }
            1 -> {
                setStartTime(data!!.getLongExtra(TimePickerDialogFragment.RESULT_TIME, 0))
            }
            2 -> {
                setEndTime(data!!.getLongExtra(TimePickerDialogFragment.RESULT_TIME, 0))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setEndTime(time: Long?) {
        if(time != null)
            endTimestamp = time
        end_time_button.text = SimpleDateFormat("HH:mm").format(Date(DateTime(logEntry!!.timestamp).withTimeAtStartOfDay().millis + endTimestamp))
    }

    private fun setStartTime(time: Long?) {
        if(time != null)
            logEntry!!.timestamp = DateTime(logEntry!!.timestamp).withTimeAtStartOfDay().millis + time
        start_time_button.text = SimpleDateFormat("HH:mm").format(Date(logEntry!!.timestamp))
    }

    private fun setDate(date: Long) {
        val dateTime = DateTime(logEntry!!.timestamp)
        val newDateTime = MutableDateTime(date)
        newDateTime.setTime(dateTime.hourOfDay, dateTime.minuteOfHour, dateTime.secondOfMinute, dateTime.millisOfSecond)
        logEntry!!.timestamp = newDateTime.millis
        date_picker_button.text = SimpleDateFormat("yyyy-MM-dd").format(Date(logEntry!!.timestamp))
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
        setDate(DateTime(if(logEntry!!.timestamp == 0L) System.currentTimeMillis() else logEntry!!.timestamp).withTimeAtStartOfDay().millis)
        logEntry!!.timestamp = System.currentTimeMillis()
        setStartTime(null)
    }
}