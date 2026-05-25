package com.example.citypulse.data.favorites

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class FavoritePlace(
    val id: String,
    val name: String,
    val category: String,
    val address: String? = null,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Double,
    val wikidata: String? = null,
)

object InMemoryFavoritesRepository : FavoritesRepository {
    private val _favorites = MutableStateFlow<Map<String, FavoritePlace>>(emptyMap())
    override val favorites: kotlinx.coroutines.flow.Flow<Map<String, FavoritePlace>> = _favorites.asStateFlow()

    override suspend fun isFavorite(placeId: String): Boolean {
        return placeId.isNotBlank() && _favorites.value.containsKey(placeId)
    }

    override suspend fun setFavorite(place: FavoritePlace, isFavorite: Boolean) {
        if (place.id.isBlank()) return

        _favorites.value = if (isFavorite) {
            _favorites.value + (place.id to place)
        } else {
            _favorites.value - place.id
        }
    }

    override suspend fun toggleFavorite(place: FavoritePlace): Boolean {
        val newFavoriteState = !isFavorite(place.id)
        setFavorite(place, newFavoriteState)
        return newFavoriteState
    }
}


