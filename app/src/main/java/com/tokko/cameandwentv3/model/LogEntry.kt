package com.tokko.cameandwentv3.model

import java.util.*

/**
 * Created by andre on 1/07/2017.
 */
class LogEntry(timeStamp: Long, entered: Boolean) {
    var id: String = UUID.randomUUID().toString()
    var timestamp: Long = timeStamp
    var entered: Boolean = entered
    var projectId: String = ""
}