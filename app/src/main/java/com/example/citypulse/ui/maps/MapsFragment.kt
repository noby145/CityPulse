package com.example.citypulse.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.citypulse.R
import com.example.citypulse.data.remote.model.Place
import com.example.citypulse.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapsViewModel by viewModels {
        MapsViewModelFactory(requireActivity().application)
    }
    private var googleMap: GoogleMap? = null
    private var mapReady = false
    private var infoWindowAdapter: MapInfoWindowAdapter? = null
    private var lastErrorMessage: String? = null  // Store last error for retry

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (isGranted) {
                enableMyLocationLayer()
                viewModel.loadCurrentLocation()
            } else {
                viewModel.onPermissionDenied()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtonListeners()

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.locationStatus.text = state.statusMessage

            // Handle places loading state
            binding.placesLoadingIndicator.visibility = if (state.placesLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }

            // Handle error state - show error container if there's an error message
            if (state.errorMessage.isNotEmpty()) {
                showError(state.errorMessage)
            } else {
                hideError()
            }

            // Update current location marker
            state.currentLocation?.let { location ->
                if (mapReady) {
                    updateMapLocation(location)
                }
            }

            // Display nearby places when they're loaded
            if (state.nearbyPlaces.isNotEmpty() && mapReady) {
                displayNearbyPlaces(state.nearbyPlaces)

                // Show places count
                val placesCount = String.format(
                    getString(R.string.places_count_format),
                    state.nearbyPlaces.size,
                )
                binding.placesCount.text = placesCount
                binding.placesCount.visibility = View.VISIBLE
            } else {
                binding.placesCount.visibility = View.GONE
            }
        }

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also { fragment ->
                childFragmentManager.beginTransaction()
                    .replace(R.id.map_container, fragment)
                    .commitNow()
            }
        supportMapFragment.getMapAsync(this)

        if (hasLocationPermission()) {
            enableMyLocationLayer()
            viewModel.loadCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    private fun setupButtonListeners() {
        binding.retryButton.setOnClickListener {
            // Retry fetching nearby places from current location
            viewModel.uiState.value?.currentLocation?.let { location ->
                viewModel.retryLoadNearbyPlaces(location.latitude, location.longitude)
            }
            hideError()
        }

        binding.dismissErrorButton.setOnClickListener {
            hideError()
        }
    }

    private fun hideError() {
        binding.errorContainer.visibility = View.GONE
    }

    private fun showError(message: String) {
        lastErrorMessage = message
        binding.errorMessage.text = message
        binding.errorContainer.visibility = View.VISIBLE
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mapReady = true

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        // Set up custom info window adapter
        infoWindowAdapter = MapInfoWindowAdapter(LayoutInflater.from(requireContext()))
        map.setInfoWindowAdapter(infoWindowAdapter)

        // Set up marker click listener
        map.setOnMarkerClickListener { marker ->
            val selectedPlace = marker.tag as? Place
            if (selectedPlace != null) {
                navigateToPlaceDetails(selectedPlace)
                true
            } else {
                marker.showInfoWindow()
                true
            }
        }

        if (hasLocationPermission()) {
            enableMyLocationLayer()
        }

        viewModel.uiState.value?.currentLocation?.let { location ->
            updateMapLocation(location)
        }
    }

    private fun enableMyLocationLayer() {
        val map = googleMap ?: return
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                map.isMyLocationEnabled = true
            } catch (_: SecurityException) {
                // The permission check above guards this, but GoogleMap still requires a runtime-safe call.
            }
        }
    }

    private fun updateMapLocation(location: LatLng) {
        val map = googleMap ?: return
        map.clear()

        // Format coordinates for display
        val coordinateText = String.format(
            Locale.US,
            "%.4f° %s, %.4f° %s",
            kotlin.math.abs(location.latitude),
            if (location.latitude >= 0) "N" else "S",
            kotlin.math.abs(location.longitude),
            if (location.longitude >= 0) "E" else "W",
        )

        // Create marker with place details in snippet
        map.addMarker(
            MarkerOptions()
                .position(location)
                .title(getString(R.string.map_marker_title))
                .snippet("Current Location|$coordinateText"),
        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
    }

    /**
     * Display nearby places as markers on the map.
     * Each marker shows the place name and can be clicked for detailed info.
     * Note: Does NOT clear existing markers (like current location).
     */
    fun displayNearbyPlaces(places: List<Place>) {
        val map = googleMap ?: return
        if (!mapReady) return

        places.forEach { place ->
            val location = LatLng(place.latitude, place.longitude)
            val coordinateText = String.format(
                Locale.US,
                "%.4f° %s, %.4f° %s",
                kotlin.math.abs(place.latitude),
                if (place.latitude >= 0) "N" else "S",
                kotlin.math.abs(place.longitude),
                if (place.longitude >= 0) "E" else "W",
            )

            val marker = map.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(place.name)
                    .snippet("${place.kinds}|$coordinateText"),
            )
            marker?.tag = place
        }
    }

    private fun navigateToPlaceDetails(place: Place) {
        val direction = MapsFragmentDirections.actionMapsFragmentToPlaceDetailFragment(
            placeId = place.id,
            placeName = place.name,
            placeCategory = place.kinds,
            address = place.address,
            latitude = place.latitude.toFloat(),
            longitude = place.longitude.toFloat(),
            distanceMeters = place.distanceMeters.toFloat(),
            wikidata = place.wikidata,
        )
        findNavController().navigate(direction)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        googleMap = null
        mapReady = false
        infoWindowAdapter = null
        _binding = null
    }
}


