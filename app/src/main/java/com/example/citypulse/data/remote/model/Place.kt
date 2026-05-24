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
    val wikidata: String? = null,
)

fun PlaceFeature.toPlace(): Place {
    val (longitude, latitude) = if (geometry.coordinates.size >= 2) {
        geometry.coordinates[0] to geometry.coordinates[1]
    } else {
        0.0 to 0.0
    }

    return Place(
        id = properties.xid,
        name = properties.name,
        latitude = latitude,
        longitude = longitude,
        kinds = properties.kinds,
        wikidata = properties.wikidata,
    )
}

