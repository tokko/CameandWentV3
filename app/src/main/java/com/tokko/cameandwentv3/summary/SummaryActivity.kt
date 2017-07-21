package com.tokko.cameandwentv3.summary

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.model.Duration
import com.tokko.cameandwentv3.model.LogEntry
import com.tokko.cameandwentv3.model.Summary
import com.tokko.cameandwentv3.settings.SettingsActivity
import com.tokko.cameandwentv3.settings.getSetting
import kotlinx.android.synthetic.main.summary_activity.*
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import java.util.*

/**
 * Created by andre on 8/07/2017.
 */
class SummaryActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.summary_activity)
    }

    override fun onStart() {
        super.onStart()
        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("logentries").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val logEntries = p0?.getValue(object: GenericTypeIndicator<HashMap<@kotlin.jvm.JvmSuppressWildcards String, @kotlin.jvm.JvmSuppressWildcards LogEntry>>(){ })?.values?.toList()
                if(logEntries != null){
                    //constructing durations
                    val startOfLastMonthMutable = MutableDateTime(System.currentTimeMillis())
                    startOfLastMonthMutable.addMonths(-1)
                    startOfLastMonthMutable.dayOfMonth = 1
                    val startOfLastMonth = startOfLastMonthMutable.toDateTime().withTimeAtStartOfDay().millis
                    val summaries = logEntries.toList()
                            .sortedBy { it.timestamp }
                            .takeLastWhile { it.timestamp > startOfLastMonth }
                            .groupBy { DateTime(it.timestamp).withDayOfWeek(1).withTimeAtStartOfDay().millis }
                            .flatMap {x -> x.value.groupBy { y -> y.projectTitle }.map { z -> Summary(x.key, z.key, z.value.groupBy { DateTime(it.timestamp).withTimeAtStartOfDay().millis }
                                    .map { Duration(it.value, getSetting().consultRounding, true) }
                                    .toList()) } }
                    vpPager.adapter = SummaryFragmentAdapter(supportFragmentManager, summaries)
                }
            }
        })
    }
}