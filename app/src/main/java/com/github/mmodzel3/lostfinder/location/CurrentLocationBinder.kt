package com.github.mmodzel3.lostfinder.location

import android.os.Binder

class CurrentLocationBinder(private val currentLocationService: CurrentLocationService) :
                Binder() {

    fun registerListener(listener: CurrentLocationListener) {
        currentLocationService.registerListener(listener)
    }

    fun unregisterListener(listener: CurrentLocationListener) {
        currentLocationService.unregisterListener(listener)
    }
}
