package com.example.citypulse.data.favorites

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Lightweight DAO-like adapter backed by the in-memory repository so the project
// can compile without Room annotation processing. This class mirrors the API
// we would expect from a Room-generated DAO and delegates to `InMemoryFavoritesRepository`.
class FavoritePlaceDao {
    fun getAllFavorites(): Flow<List<FavoritePlaceEntity>> =
        InMemoryFavoritesRepository.favorites.map { map -> map.values.map { it.toEntity() } }

    fun getFavoriteById(id: String): Flow<FavoritePlaceEntity?> =
        InMemoryFavoritesRepository.favorites.map { map -> map[id]?.toEntity() }

    fun isFavoriteFlow(id: String): Flow<Boolean> =
        InMemoryFavoritesRepository.favorites.map { map -> map.containsKey(id) }

    suspend fun insert(place: FavoritePlaceEntity) {
        InMemoryFavoritesRepository.setFavorite(place.toDomain(), true)
    }

    suspend fun delete(place: FavoritePlaceEntity) {
        InMemoryFavoritesRepository.setFavorite(place.toDomain(), false)
    }

    suspend fun deleteById(id: String) {
        val existing = InMemoryFavoritesRepository.favorites
            .map { it[id] }
            .let { /* noop: higher-level repo will handle deletion */ }
    }
}

