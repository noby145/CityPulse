package com.example.citypulse.ui.places

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citypulse.data.favorites.FavoritePlace
import com.example.citypulse.data.favorites.FavoritesRepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class PlaceDetailUiState(
    val placeId: String = "",
    val placeName: String = "",
    val placeCategory: String = "",
    val address: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distanceMeters: Double = 0.0,
    val wikidata: String? = null,
    val notes: String = "",
    val isFavorite: Boolean = false,
)

class PlaceDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<PlaceDetailUiState> = _uiState.asStateFlow()

    val currentNotes: String
        get() = _uiState.value.notes

    init {
        viewModelScope.launch {
            FavoritesRepositoryProvider.provide().favorites.collect { favorites ->
                val currentPlaceId = _uiState.value.placeId
                if (currentPlaceId.isNotBlank()) {
                    val isFavorite = favorites.containsKey(currentPlaceId)
                    if (isFavorite != _uiState.value.isFavorite) {
                        _uiState.value = _uiState.value.copy(isFavorite = isFavorite)
                    }
                }
            }
        }
    }

    private fun buildInitialState(): PlaceDetailUiState {
        val placeId = savedStateHandle.get<String>(KEY_PLACE_ID).orEmpty()
        return PlaceDetailUiState(
            placeId = placeId,
            placeName = savedStateHandle.get<String>(KEY_PLACE_NAME).orEmpty(),
            placeCategory = savedStateHandle.get<String>(KEY_PLACE_CATEGORY).orEmpty(),
            address = savedStateHandle.get<String>(KEY_ADDRESS),
            latitude = savedStateHandle.get<Float>(KEY_LATITUDE)?.toDouble() ?: 0.0,
            longitude = savedStateHandle.get<Float>(KEY_LONGITUDE)?.toDouble() ?: 0.0,
            distanceMeters = savedStateHandle.get<Float>(KEY_DISTANCE_METERS)?.toDouble() ?: 0.0,
            wikidata = savedStateHandle.get<String>(KEY_WIKIDATA),
            notes = savedStateHandle.get<String>(KEY_NOTES).orEmpty(),
            // initial favorite state is false; repository collector will update state when available
            isFavorite = false,
        )
    }

    fun updateNotes(notes: String) {
        val sanitizedNotes = notes.trimEnd()
        if (sanitizedNotes == _uiState.value.notes) return

        savedStateHandle[KEY_NOTES] = sanitizedNotes
        _uiState.value = _uiState.value.copy(notes = sanitizedNotes)
    }

    fun toggleFavorite() {
        val state = _uiState.value
        if (state.placeId.isBlank()) return

        viewModelScope.launch {
            val repo = FavoritesRepositoryProvider.provide()
            val newFavoriteState = repo.toggleFavorite(state.toFavoritePlace())
            _uiState.value = state.copy(isFavorite = newFavoriteState)
        }
    }

    fun buildShareText(): String {
        val state = _uiState.value
        val coordinatesText = String.format(
            Locale.US,
            "%.5f, %.5f",
            state.latitude,
            state.longitude,
        )
        val googleMapsLink = buildGoogleMapsLink(state.latitude, state.longitude)

        return buildString {
            appendLine(state.placeName.ifBlank { "Unknown place" })
            appendLine("Coordinates: $coordinatesText")
            appendLine("Google Maps: $googleMapsLink")
        }.trim()
    }

    private fun buildGoogleMapsLink(latitude: Double, longitude: Double): String {
        return "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
    }

    private fun PlaceDetailUiState.toFavoritePlace(): FavoritePlace {
        return FavoritePlace(
            id = placeId,
            name = placeName,
            category = placeCategory,
            address = address,
            latitude = latitude,
            longitude = longitude,
            distanceMeters = distanceMeters,
            wikidata = wikidata,
        )
    }

    private companion object {
        const val KEY_PLACE_ID = "placeId"
        const val KEY_PLACE_NAME = "placeName"
        const val KEY_PLACE_CATEGORY = "placeCategory"
        const val KEY_ADDRESS = "address"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_DISTANCE_METERS = "distanceMeters"
        const val KEY_WIKIDATA = "wikidata"
        const val KEY_NOTES = "place_detail_notes"
    }
}


