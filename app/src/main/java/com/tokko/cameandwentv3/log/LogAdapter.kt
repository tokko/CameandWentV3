package com.tokko.cameandwentv3.log

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListAdapter
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
    val entries = ArrayList<LogEntry>()
    fun clear(){
        entries.clear()
    }

    fun addAll(entries: Collection<LogEntry>){
        clear()
        this.entries.addAll(entries)
    }
    override fun getChildrenCount(groupPosition: Int): Int {
        return entries.size
    }

    override fun getGroup(groupPosition: Int): Duration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGroupCollapsed(groupPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerDataSetObserver(observer: DataSetObserver?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChild(groupPosition: Int, childPosition: Int): LogEntry {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGroupExpanded(groupPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCombinedChildId(groupId: Long, childId: Long): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupId(groupPosition: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasStableIds(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val v = convertView ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.log_entry, null)
        val item = getChild(groupPosition, childPosition)
        (v.findViewById(R.id.timestamp) as TextView).text = SimpleDateFormat("HH:mm:ss").format(item?.timestamp) ?: ""
        (v.findViewById(R.id.action) as TextView).text = if(item.entered) "Arrived" else "Departed"
        (v.findViewById(R.id.project_name) as TextView).text = item?.projectTitle
        return v
    }

    override fun areAllItemsEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCombinedGroupId(groupId: Long): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}