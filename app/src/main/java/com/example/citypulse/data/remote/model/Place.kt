package com.example.citypulse.data.remote.model

import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @SerializedName("features")
    val features: List<PlaceFeature> = emptyList(),
)

data class PlaceFeature(
    @SerializedName("type")
    val type: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("geometry")
    val geometry: Geometry = Geometry(),
    @SerializedName("properties")
    val properties: PlaceProperties = PlaceProperties(),
)

data class Geometry(
    @SerializedName("type")
    val type: String = "",
    @SerializedName("coordinates")
    val coordinates: List<Double> = emptyList(),
)

data class PlaceProperties(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("xid")
    val xid: String = "",
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("wikidata")
    val wikidata: String? = null,
    @SerializedName("kinds")
    val kinds: String = "",
)

data class Place(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val kinds: String,
    val address: String? = null,
    val wikidata: String? = null,
    val distanceMeters: Double = 0.0,
)

fun PlaceFeature.toPlace(originLatitude: Double, originLongitude: Double): Place {
    val (longitude, latitude) = if (geometry.coordinates.size >= 2) {
        geometry.coordinates[0] to geometry.coordinates[1]
    } else {
        0.0 to 0.0
    }

    val distanceMeters = haversineDistanceMeters(
        startLatitude = originLatitude,
        startLongitude = originLongitude,
        endLatitude = latitude,
        endLongitude = longitude,
    )

    return Place(
        id = properties.xid,
        name = properties.name,
        latitude = latitude,
        longitude = longitude,
        kinds = properties.kinds,
        address = properties.address,
        wikidata = properties.wikidata,
        distanceMeters = distanceMeters,
    )
}

private fun haversineDistanceMeters(
    startLatitude: Double,
    startLongitude: Double,
    endLatitude: Double,
    endLongitude: Double,
): Double {
    val earthRadiusMeters = 6_371_000.0
    val lat1 = Math.toRadians(startLatitude)
    val lat2 = Math.toRadians(endLatitude)
    val deltaLat = Math.toRadians(endLatitude - startLatitude)
    val deltaLon = Math.toRadians(endLongitude - startLongitude)

    val a = kotlin.math.sin(deltaLat / 2).let { sinLat ->
        val sinLon = kotlin.math.sin(deltaLon / 2)
        sinLat * sinLat + kotlin.math.cos(lat1) * kotlin.math.cos(lat2) * sinLon * sinLon
    }
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return earthRadiusMeters * c
}

