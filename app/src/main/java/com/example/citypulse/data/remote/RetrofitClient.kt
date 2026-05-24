package com.example.citypulse.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit HTTP client for OpenTripMap API.
 *
 * Base URL: https://api.opentripmap.com/0.1/
 *
 * Endpoints:
 * - GET /places/radius - Find places within a radius
 *   Parameters: lat, lon, radius, apikey, kinds (optional), limit (optional)
 *   Returns: GeoJSON FeatureCollection
 */
object RetrofitClient {
    // OpenTripMap API base URL - note: NO trailing slash needed, endpoint will add path
    // private const val BASE_URL = "https://api.opentripmap.com"
    private const val BASE_URL = "https://api.opentripmap.com/0.1/en/"
    private const val TIMEOUT_SECONDS = 15L

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}

