package com.example.citypulse.ui.places

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import com.example.citypulse.R
import com.example.citypulse.data.remote.model.Place
import com.example.citypulse.data.repository.PlacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlacesUiState(
    val isLoading: Boolean = false,
    val places: List<Place> = emptyList(),
    val errorMessage: String = "",
)

class PlacesViewModel(application: Application) : AndroidViewModel(application) {
    private val placesRepository = PlacesRepository()

    private val _uiState = MutableStateFlow(PlacesUiState())
    val uiState: StateFlow<PlacesUiState> = _uiState.asStateFlow()

    fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radius: Int = 5000,
        kinds: String = "",
        limit: Int = 50,
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = "",
        )

        viewModelScope.launch(Dispatchers.IO) {
            val result = placesRepository.getNearbyPlaces(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                kinds = kinds,
                limit = limit,
            )

            result.onSuccess { places ->
                _uiState.value = PlacesUiState(
                    isLoading = false,
                    places = places,
                    errorMessage = "",
                )
            }.onFailure { exception ->
                _uiState.value = PlacesUiState(
                    isLoading = false,
                    places = emptyList(),
                    errorMessage = exception.message
                        ?: getApplication<Application>().getString(R.string.unable_to_load_places),
                )
            }
        }
    }

    fun clearPlaces() {
        _uiState.value = PlacesUiState()
    }
}

