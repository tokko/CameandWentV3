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
    var locations: ArrayList<ProjectLocation> = ArrayList()
    var SSIDs: ArrayList<String> = ArrayList()

    override fun toString(): String {
        return title
    }

    fun addLocation(location: Location) {
        locations.add(ProjectLocation(location.latitude, location.latitude))
    }

    class ProjectLocation(): Serializable{
        constructor(latitude: Double, longitude: Double): this(){
            this.latitude = latitude
            this.longitude = longitude
        }
        var id : String = UUID.randomUUID().toString()
        var latitude: Double = 0.0
        var longitude: Double = 0.0
    }
}