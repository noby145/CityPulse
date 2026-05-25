package com.example.citypulse.data.favorites

import android.content.Context

object FavoritesRepositoryProvider {
    // Default to in-memory implementation for tests / before initialization
    @Volatile
    private var repository: FavoritesRepository = InMemoryFavoritesRepository

    fun initializeWithRoom(context: Context) {
        val db = CityPulseDatabase.getInstance(context.applicationContext)
        repository = RoomFavoritesRepository(db.favoritePlaceDao())
    }

    fun provide(): FavoritesRepository = repository
}
