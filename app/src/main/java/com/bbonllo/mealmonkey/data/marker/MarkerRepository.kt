package com.bbonllo.mealmonkey.data.marker

import androidx.lifecycle.LiveData

class MarkerRepository(private val markerDAO: MarkerDAO) {

    val readAllMarkers: LiveData<List<Marker>> = markerDAO.readAllMarkers()

    suspend fun addMarker(marker: Marker) {
        markerDAO.addMarker(marker)
    }

}