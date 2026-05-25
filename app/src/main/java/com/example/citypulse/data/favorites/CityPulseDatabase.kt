package com.example.citypulse.data.favorites

import android.content.Context

// Minimal replacement for a Room database. This shim provides a `favoritePlaceDao()`
// backed by the in-memory repository so callers can obtain a DAO-like object even
// when Room isn't available during compilation.
class CityPulseDatabase private constructor(private val dao: FavoritePlaceDao) {
    fun favoritePlaceDao(): FavoritePlaceDao = dao

    companion object {
        @Volatile
        private var INSTANCE: CityPulseDatabase? = null

        fun getInstance(context: Context): CityPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = CityPulseDatabase(FavoritePlaceDao())
                INSTANCE = instance
                instance
            }
        }
    }
}