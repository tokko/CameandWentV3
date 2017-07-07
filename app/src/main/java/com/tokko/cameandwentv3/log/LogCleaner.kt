package com.tokko.cameandwentv3.log

import com.tokko.cameandwentv3.model.LogEntry
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by andreas on 7/07/17.
 */
class LogCleaner {
    fun clean(logEntries: List<LogEntry>) : List<LogEntry>{
        val stack = Stack<LogEntry>()
        val ret = ArrayList<LogEntry>()
        stack.addAll(logEntries.sortedBy { x -> x.timestamp }.asIterable())
        if(stack.size == 1 && !stack.peek().entered){
            ret.add(stack.pop())
            return ret
        }
        while(!stack.empty()){
            val top = stack.pop()
            if(stack.empty()) break
            if(top.entered == stack.peek().entered)
                ret.add(top)
        }
        return ret
    }
}