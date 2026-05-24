package com.example.citypulse.ui.maps

import android.view.LayoutInflater
import android.view.View
import com.example.citypulse.databinding.MapInfoWindowBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MapInfoWindowAdapter(private val inflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {
    private var binding: MapInfoWindowBinding? = null

    override fun getInfoWindow(marker: Marker): View? {
        return null  // Use default window frame
    }

    override fun getInfoContents(marker: Marker): View? {
        binding = MapInfoWindowBinding.inflate(inflater)
        val view = binding?.root ?: return null

        // Extract place details from marker tag or snippet
        val title = marker.title ?: "Unknown Place"
        val snippet = marker.snippet ?: ""
        val details = snippet.split("|")

        binding?.infoWindowTitle?.text = title
        binding?.infoWindowCategory?.text = details.getOrNull(0)?.trim() ?: "Other"
        binding?.infoWindowCoordinates?.text = details.getOrNull(1)?.trim() ?: ""

        return view
    }
}


