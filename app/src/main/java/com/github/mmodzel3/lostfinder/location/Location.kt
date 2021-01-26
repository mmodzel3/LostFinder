package com.github.mmodzel3.lostfinder.location

import org.osmdroid.util.GeoPoint

data class Location(val latitude: Double,
                    val longitude: Double) {

    fun toGeoPoint(): GeoPoint {
        return GeoPoint(latitude, longitude)
    }
}