package com.example.citypulse

import android.app.Application
import com.example.citypulse.data.favorites.FavoritesRepositoryProvider

class CityPulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the favorites repository with Room backing store
        FavoritesRepositoryProvider.initializeWithRoom(this)
    }
}
