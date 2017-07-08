package com.tokko.cameandwentv3.summary

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.tokko.cameandwentv3.model.Summary

/**
 * Created by andre on 8/07/2017.
 */
class SummaryFragmentAdapter(fm: FragmentManager, val summaries: List<Summary>): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return SummaryFragment.newInstance(summaries[position])
    }

    override fun getCount(): Int {
        return summaries.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return summaries[position].project + " W:" + summaries[position].weekNumber
    }
}