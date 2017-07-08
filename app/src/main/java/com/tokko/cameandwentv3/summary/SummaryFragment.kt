package com.tokko.cameandwentv3.summary

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.model.Duration
import com.tokko.cameandwentv3.model.Summary
import kotlinx.android.synthetic.main.summary_fragment.*

/**
 * Created by andre on 8/07/2017.
 */
class SummaryFragment: Fragment() {
    var summary: Summary? = null
    var adapter: ArrayAdapter<Duration>? = null
    companion object {
        val ARG_SUMMARY = "summary"
        fun newInstance(summary : Summary): SummaryFragment{
            val b = Bundle()
            b.putSerializable(ARG_SUMMARY, summary)
            val f = SummaryFragment()
            f.arguments = b
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = savedInstanceState ?: arguments
        if(args != null){
            summary = args.getSerializable(ARG_SUMMARY) as? Summary
        }
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.summary_fragment, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        week_number?.text = "Week: " + summary?.weekNumber
        projectview?.text = "Project: " + summary?.project
        adapter = object: ArrayAdapter<Duration>(activity, R.layout.duration){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val v = convertView ?: (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.duration, null, false)
                v.findViewById<TextView>(R.id.date).text = getItem(position).date
                v.findViewById<TextView>(R.id.duration).text = getItem(position).duration
                return v
            }
        }
        if(view != null) {
            view.findViewById<ListView>(R.id.list)?.adapter = adapter
        }
        adapter!!.addAll(summary!!.durations)
        adapter!!.notifyDataSetChanged()
    }
}