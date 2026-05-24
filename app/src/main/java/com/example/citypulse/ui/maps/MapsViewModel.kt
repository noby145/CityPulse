package com.example.citypulse.ui.maps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.citypulse.R
import com.example.citypulse.data.location.LocationRepository
import com.example.citypulse.data.repository.PlacesRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRepository = LocationRepository(application)
    private val placesRepository = PlacesRepository()

    private val _uiState = MutableLiveData(
        MapsUiState(
            isLoading = true,
            statusMessage = application.getString(R.string.map_loading),
        ),
    )
    val uiState: LiveData<MapsUiState> = _uiState

    private var hasQueriedPlaces = false  // Flag to prevent duplicate API calls

    fun loadCurrentLocation() {
        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            statusMessage = getApplication<Application>().getString(R.string.map_loading),
        )

        locationRepository.getCurrentLocation(
            onSuccess = { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                _uiState.postValue(
                    MapsUiState(
                        isLoading = false,
                        currentLocation = latLng,
                        statusMessage = getApplication<Application>().getString(
                            R.string.map_location_format,
                            latLng.latitude,
                            latLng.longitude,
                        ),
                        nearbyPlaces = _uiState.value?.nearbyPlaces ?: emptyList(),
                        placesLoading = _uiState.value?.placesLoading ?: false,
                    ),
                )

                // Automatically fetch nearby places after location is detected
                // Use flag to prevent duplicate calls during configuration changes
                if (!hasQueriedPlaces) {
                    hasQueriedPlaces = true
                    loadNearbyPlaces(latLng.latitude, latLng.longitude)
                }
            },
            onError = { message ->
                _uiState.postValue(
                    MapsUiState(
                        isLoading = false,
                        currentLocation = null,
                        statusMessage = message.ifBlank {
                            getApplication<Application>().getString(R.string.map_location_unavailable)
                        },
                        nearbyPlaces = _uiState.value?.nearbyPlaces ?: emptyList(),
                        placesLoading = false,
                    ),
                )
            },
        )
    }

    private fun loadNearbyPlaces(latitude: Double, longitude: Double) {
        _uiState.value = _uiState.value?.copy(placesLoading = true)

        viewModelScope.launch(Dispatchers.IO) {
            val result = placesRepository.getNearbyPlaces(
                latitude = latitude,
                longitude = longitude,
                radius = 5000,
                kinds = "",
                limit = 50,
            )

            result.onSuccess { places ->
                _uiState.postValue(
                    MapsUiState(
                        isLoading = false,
                        currentLocation = _uiState.value?.currentLocation,
                        statusMessage = _uiState.value?.statusMessage ?: "",
                        nearbyPlaces = places,
                        placesLoading = false,
                        errorMessage = "",  // Clear any previous errors
                    ),
                )
            }.onFailure { exception ->
                val errorMsg = exception.message ?: getApplication<Application>()
                    .getString(R.string.unable_to_load_places)
                
                _uiState.postValue(
                    MapsUiState(
                        isLoading = false,
                        currentLocation = _uiState.value?.currentLocation,
                        statusMessage = _uiState.value?.statusMessage ?: "",
                        nearbyPlaces = emptyList(),
                        placesLoading = false,
                        errorMessage = errorMsg,
                    ),
                )
            }
        }
    }

    fun onPermissionDenied() {
        _uiState.value = MapsUiState(
            isLoading = false,
            currentLocation = null,
            statusMessage = getApplication<Application>().getString(R.string.map_permission_denied),
        )
    }

    /**
     * Public method to retry loading nearby places.
     * Used when user clicks the retry button on error state.
     */
    fun retryLoadNearbyPlaces(latitude: Double, longitude: Double) {
        loadNearbyPlaces(latitude, longitude)
    }
}


