package com.github.mmodzel3.lostfinder.location

import android.location.Location

interface CurrentLocationListener {
    fun onLocalisationChange(location: Location)
}
