package com.example.citypulse.data.favorites

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favorites: Flow<Map<String, FavoritePlace>>

    suspend fun isFavorite(placeId: String): Boolean

    suspend fun setFavorite(place: FavoritePlace, isFavorite: Boolean)

    suspend fun toggleFavorite(place: FavoritePlace): Boolean
}
