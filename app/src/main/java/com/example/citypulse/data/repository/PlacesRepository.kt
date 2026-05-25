package com.example.citypulse.data.repository

import com.example.citypulse.BuildConfig
import com.example.citypulse.data.remote.OpenTripMapService
import com.example.citypulse.data.remote.RetrofitClient
import com.example.citypulse.data.remote.model.Place
import com.example.citypulse.data.remote.model.toPlace
import kotlin.Result

/**
 * Repository for accessing OpenTripMap nearby places API.
 * Handles API calls, error conversion, and result mapping.
 */
class PlacesRepository {
    private val openTripMapService: OpenTripMapService by lazy {
        RetrofitClient.createService(OpenTripMapService::class.java)
    }

    /**
     * Get nearby places within a specified radius.
     *
     * @param latitude Center point latitude (required)
     * @param longitude Center point longitude (required)
     * @param radius Search radius in meters (required, default: 5000 meters = 5 km)
     * @param kinds Comma-separated place kinds to filter (optional)
     * @param limit Maximum number of results to return (optional, default: 50)
     * @return Result containing list of Place objects or error
     */
    suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radius: Int = 5000,
        kinds: String = "",
        limit: Int = 50,
    ): Result<List<Place>> = try {
        val apiKey = BuildConfig.OPEN_TRIP_MAP_API_KEY
        if (apiKey.isBlank() || apiKey == "YOUR_OPEN_TRIP_MAP_API_KEY") {
            Result.failure(
                IllegalStateException(
                    "OpenTripMap API key not configured. " +
                    "Get a free key from https://opentripmap.com/product and " +
                    "set it in BuildConfig.OPEN_TRIP_MAP_API_KEY"
                )
            )
        } else {
            try {
                val response = openTripMapService.getNearbyPlaces(
                    latitude = latitude,
                    longitude = longitude,
                    radius = radius,
                    apiKey = apiKey,
                    // Use takeIf to pass null if the string is empty, avoiding "Unknown category" 400 error
                    kinds = kinds.takeIf { it.isNotBlank() },
                    limit = limit,
                    skip = 0, // Replaced TODO() with 0 to prevent NotImplementedError
                )
                val places = response.features
                    .map { feature -> feature.toPlace(latitude, longitude) }
                    .sortedBy { it.distanceMeters }
                Result.success(places)
            } catch (e: Exception) {
                // Add more context to the error for debugging
                val errorMsg = when {
                    e.message?.contains("404") == true -> 
                        "API endpoint not found (404). This may indicate an incorrect API version or endpoint path."
                    e.message?.contains("401") == true || e.message?.contains("403") == true -> 
                        "API authentication failed. Check your API key is valid."
                    e.message?.contains("429") == true -> 
                        "Rate limit exceeded. Too many requests to the API."
                    else -> e.message ?: "Unknown error loading places"
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
