package com.tokko.cameandwentv3.model

import android.location.Location
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by andre on 28/06/2017.
 */
class Project : Serializable{
    var id : String = UUID.randomUUID().toString()
    var title : String = ""
    var locations: List<Location> = ArrayList()
    var SSIDs: List<String> = ArrayList()

    override fun toString(): String {
        return title
    }

}