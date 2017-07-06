package com.tokko.cameandwentv3.log

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.model.Duration
import com.tokko.cameandwentv3.model.LogEntry
import java.text.SimpleDateFormat

/**
 * Created by andreas on 6/07/17.
 */
class LogAdapter(context: Context): BaseExpandableListAdapter() {
    val context: Context = context.applicationContext
    val durations = ArrayList<Duration>()
    val observers = ArrayList<DataSetObserver>()
    fun clear(){
        durations.clear()
    }

    fun addAll(entries: Collection<Duration>){
        clear()
        this.durations.addAll(entries)
    }
    override fun getChildrenCount(groupPosition: Int): Int {
        return durations.size
    }

    override fun getGroup(groupPosition: Int): Duration {
        return durations[groupPosition]
    }

    override fun onGroupCollapsed(groupPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEmpty(): Boolean {
        return durations.isEmpty()
    }

    override fun registerDataSetObserver(observer: DataSetObserver?) {
        if(observer != null)
            observers.add(observer)
    }

    override fun getChild(groupPosition: Int, childPosition: Int): LogEntry {
        return getGroup(groupPosition).logs[childPosition]
    }

    override fun onGroupExpanded(groupPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCombinedChildId(groupId: Long, childId: Long): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val v = convertView ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.log_entry, null)
        val item = getChild(groupPosition, childPosition)
        (v.findViewById(R.id.timestamp) as TextView).text = SimpleDateFormat("HH:mm:ss").format(item.timestamp) ?: ""
        (v.findViewById(R.id.action) as TextView).text = if(item.entered) "Arrived" else "Departed"
        (v.findViewById(R.id.project_name) as TextView).text = item.projectTitle
        return v
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return (groupPosition xor childPosition).toLong()
    }

    override fun getCombinedGroupId(groupId: Long): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val v = convertView ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.duration, null)
        val item = getGroup(groupPosition)
        (v.findViewById(R.id.timestamp) as TextView).text = SimpleDateFormat("HH:mm:ss").format(item.date) ?: ""
        (v.findViewById(R.id.action) as TextView).text = item.duration
        return v
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        if(observer != null)
            observers.remove(observer)
    }

    override fun getGroupCount(): Int {
        return durations.size
    }
}