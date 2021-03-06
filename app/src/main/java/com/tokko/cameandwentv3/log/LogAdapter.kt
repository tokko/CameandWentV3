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
import java.util.*

/**
 * Created by andreas on 6/07/17.
 */
class LogAdapter(context: Context): BaseExpandableListAdapter() {
    val context: Context = context.applicationContext
    val durations = ArrayList<Duration>()
    val observers = ArrayList<DataSetObserver>()
    fun clear(){
        durations.clear()
        observers.forEach { x -> x.onInvalidated() }
    }

    fun addAll(entries: Collection<Duration>){
        clear()
        this.durations.addAll(entries)
        observers.forEach { x-> x.onChanged() }
    }
    override fun getChildrenCount(groupPosition: Int): Int {
        return durations[groupPosition].logs.size
    }

    override fun getGroup(groupPosition: Int): Duration {
        return durations[groupPosition]
    }

    override fun onGroupCollapsed(groupPosition: Int) {
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
    }

    override fun getCombinedChildId(groupId: Long, childId: Long): Long {
        return (groupId xor childId)
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
        v.findViewById<TextView>(R.id.timestamp).text = SimpleDateFormat("HH:mm:ss").format(item.timestamp) ?: ""
        v.findViewById<TextView>(R.id.action).text = if(item.entered) "Arrived" else "Departed"
        v.findViewById<TextView>(R.id.project_name).text = item.projectTitle
        return v
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return (groupPosition xor childPosition).toLong()
    }

    override fun getCombinedGroupId(groupId: Long): Long {
        return groupId
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val v = convertView ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(android.R.layout.simple_expandable_list_item_2, null)
        val item = getGroup(groupPosition)
        v.findViewById<TextView>(android.R.id.text1).text = item.date
        v.findViewById<TextView>(android.R.id.text2).text = item.duration
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