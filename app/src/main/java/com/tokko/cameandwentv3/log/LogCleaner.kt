package com.tokko.cameandwentv3.log

import com.tokko.cameandwentv3.model.LogEntry
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by andreas on 7/07/17.
 */
class LogCleaner {
    fun clean(logEntries: List<LogEntry>) : Pair<List<LogEntry>, List<LogEntry>>{
        val stack = Stack<LogEntry>()
        val ret = ArrayList<LogEntry>()
        ret.addAll(logEntries.sortedBy { it.timestamp }.takeWhile { !it.entered })
        stack.addAll(logEntries.sortedBy { x -> x.timestamp }.asIterable())

        while(!stack.empty()){
            val top = stack.pop()
            if(stack.empty()) break
            if(top.entered == stack.peek().entered)
                ret.add(top)
        }
        val cleanedList = ArrayList(logEntries)
        cleanedList.removeAll(ret)
        return Pair(cleanedList, ret)
    }
}