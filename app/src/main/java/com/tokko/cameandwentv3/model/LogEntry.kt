package com.tokko.cameandwentv3.model

import java.io.Serializable
import java.util.*

/**
 * Created by andre on 1/07/2017.
 */
class LogEntry(timeStamp: Long, entered: Boolean, projectId: String?, projectTitle: String?): Serializable {
    constructor() : this(0, true, "", "")
    var id: String = UUID.randomUUID().toString()
    var timestamp: Long = timeStamp
    var entered: Boolean = entered
    var projectId: String = projectId ?: ""
    var projectTitle: String = projectTitle ?: ""

    override fun equals(other: Any?): Boolean {
        if(other is LogEntry)
            return id == other.id
        return false
    }
}