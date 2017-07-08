package com.tokko.cameandwentv3.model

import org.joda.time.DateTime
import java.io.Serializable

/**
 * Created by andre on 8/07/2017.
 */
class Summary(weekNumberInMillis: Long, val project: String, val durations: List<Duration>) : Serializable {
    val weekNumber = DateTime(weekNumberInMillis).weekOfWeekyear
}