package com.example.citypulse.data.favorites

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RoomFavoritesRepository(
    private val dao: FavoritePlaceDao,
) : FavoritesRepository {

    override val favorites: Flow<Map<String, FavoritePlace>> = dao.getAllFavorites()
        .map { list -> list.associateBy({ it.id }, { it.toDomain() }) }

    override suspend fun isFavorite(placeId: String): Boolean {
        // Use the DAO flow and take first emission; simple implementation: collect single value
        return dao.getFavoriteById(placeId).map { it != null }.first()
    }

    override suspend fun setFavorite(place: FavoritePlace, isFavorite: Boolean) {
        if (place.id.isBlank()) return
        if (isFavorite) {
            dao.insert(place.toEntity())
        } else {
            dao.deleteById(place.id)
        }
    }

    override suspend fun toggleFavorite(place: FavoritePlace): Boolean {
        val currently = isFavorite(place.id)
        setFavorite(place, !currently)
        return !currently
    }
}

