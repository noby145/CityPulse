package com.example.citypulse.data.remote

import com.example.citypulse.data.remote.model.PlaceResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * OpenTripMap API service for retrieving nearby places.
 */
interface OpenTripMapService {
    /**
     * Get nearby places within a radius around a geographic point.
     *
     * @param latitude Center point latitude
     * @param longitude Center point longitude
     * @param radius Search radius in meters
     * @param apiKey OpenTripMap API key
     * @param kinds Filter by place kinds (optional). If null or empty, Retrofit should omit this parameter.
     * @param limit Maximum number of results to return
     * @param skip Number of results to skip
     */
    @GET("places/radius")
    suspend fun getNearbyPlaces(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("radius") radius: Int,
        @Query("apikey") apiKey: String,
        @Query("kinds") kinds: String?,
        @Query("limit") limit: Int?,
        @Query("skip") skip: Int?,
    ): PlaceResponse
}
