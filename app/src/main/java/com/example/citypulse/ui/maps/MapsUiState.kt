package com.example.citypulse.ui.maps

import com.example.citypulse.data.remote.model.Place
import com.google.android.gms.maps.model.LatLng

data class MapsUiState(
    val isLoading: Boolean = true,
    val currentLocation: LatLng? = null,
    val statusMessage: String = "",
    val nearbyPlaces: List<Place> = emptyList(),
    val placesLoading: Boolean = false,
    val errorMessage: String = "",
)

