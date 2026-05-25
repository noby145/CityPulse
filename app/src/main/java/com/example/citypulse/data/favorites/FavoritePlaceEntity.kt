package com.example.citypulse.data.favorites

// A lightweight entity-like data class used by the in-memory/room adapter.
// We avoid Room annotations here so the project can compile even when Room
// annotation processing isn't configured. This file mirrors the intended
// schema for a Room entity.
data class FavoritePlaceEntity(
    val id: String,
    val name: String,
    val category: String,
    val address: String? = null,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Double,
    val wikidata: String? = null,
)

fun FavoritePlaceEntity.toDomain(): FavoritePlace = FavoritePlace(
    id = id,
    name = name,
    category = category,
    address = address,
    latitude = latitude,
    longitude = longitude,
    distanceMeters = distanceMeters,
    wikidata = wikidata,
)

fun FavoritePlace.toEntity(): FavoritePlaceEntity = FavoritePlaceEntity(
    id = id,
    name = name,
    category = category,
    address = address,
    latitude = latitude,
    longitude = longitude,
    distanceMeters = distanceMeters,
    wikidata = wikidata,
)

