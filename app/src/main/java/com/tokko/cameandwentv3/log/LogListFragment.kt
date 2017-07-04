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
                (v.findViewById(R.id.project_name) as TextView).text = item?.projectId
                return v
            }
        }
        clock_button.setOnCheckedChangeListener { _, _ ->
            val projectPicker = ProjectPickerDialog()
            projectPicker.setTargetFragment(this, 0)
            projectPicker.show(activity.fragmentManager, "some tag")
        }
        loglist.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            0 -> {
                val duration = System.currentTimeMillis()
                val hours = TimeUnit.MILLISECONDS.toHours(duration)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours))
                val seconds = TimeUnit.MILLISECONDS.toSeconds(duration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
                val durationString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                val p = data!!.getSerializableExtra("project") as Project
                adapter!!.add(LogEntry(System.currentTimeMillis(), clock_button.isChecked, p.title))
                adapter!!.notifyDataSetChanged()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}