# OpenTripMap API Endpoint - Radius Search

This document describes the corrected OpenTripMap API implementation for finding nearby places using radius-based search.

## Endpoint Overview

**API Base URL**: `https://api.opentripmap.com/0.1/`

**Endpoint**: `GET /places/radius`

**Purpose**: Find places within a specified radius around a geographic point (latitude, longitude).

## Required Parameters

| Parameter | Type | Description | Range/Format |
|-----------|------|-------------|--------------|
| `lat` | Double | Center point latitude | -90 to 90 |
| `lon` | Double | Center point longitude | -180 to 180 |
| `radius` | Integer | Search radius in meters | 200 to 50000 (recommended) |
| `apikey` | String | OpenTripMap API key | Any valid API key |

## Optional Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `kinds` | String | "" (all) | Comma-separated place types to filter |
| `limit` | Integer | 50 | Max results to return (1-10000) |
| `skip` | Integer | 0 | Number of results to skip (pagination) |

## Response Format

**Content-Type**: `application/json`

**Format**: GeoJSON FeatureCollection

```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "id": "Q123456",
      "geometry": {
        "type": "Point",
        "coordinates": [longitude, latitude]
      },
      "properties": {
        "name": "Place Name",
        "xid": "Q123456",
        "kinds": "natural,historic",
        "wikidata": "Q123456"
      }
    }
  ]
}
```

## Example Request

```
GET https://api.opentripmap.com/0.1/places/radius?lat=37.7749&lon=-122.4194&radius=5000&apikey=YOUR_KEY&limit=50
```

## Implementation in CityPulse

### Retrofit Service Interface

```kotlin
@GET("places/radius")
suspend fun getNearbyPlaces(
    @Query("lat") latitude: Double,
    @Query("lon") longitude: Double,
    @Query("radius") radius: Int,           // Required, no default
    @Query("apikey") apiKey: String,        // Required
    @Query("kinds") kinds: String = "",     // Optional
    @Query("limit") limit: Int = 50,        // Optional
    @Query("skip") skip: Int = 0,           // Optional for pagination
): PlaceResponse
```

### Repository Usage

```kotlin
val result = openTripMapService.getNearbyPlaces(
    latitude = 37.7749,
    longitude = -122.4194,
    radius = 5000,              // 5 km radius
    apiKey = BuildConfig.OPEN_TRIP_MAP_API_KEY,
    kinds = "natural,historic",  // Optional filter
    limit = 50                   // Return up to 50 places
)
```

### ViewModel Integration

```kotlin
fun loadNearbyPlaces(latitude: Double, longitude: Double) {
    val result = placesRepository.getNearbyPlaces(
        latitude = latitude,
        longitude = longitude,
        radius = 5000,  // 5 km is default
    )
}
```

## Place Kinds Filter

Optional comma-separated values for the `kinds` parameter:

### Natural/Outdoor
- `natural` - Natural features (mountains, forests, etc.)
- `beach` - Beaches
- `mountain` - Mountains
- `park` - Parks and gardens
- `water` - Water bodies

### Historical/Cultural
- `historic` - Historic sites
- `monument` - Monuments
- `museum` - Museums
- `temple` - Religious buildings
- `castle` - Castles and fortifications
- `archaeological` - Archaeological sites

### Commercial/Amenities
- `restaurant` - Restaurants
- `cafe` - Cafes
- `hotel` - Hotels
- `shop` - Shops
- `bank` - Banks
- `hospital` - Hospitals

### Entertainment
- `cinema` - Cinemas
- `theatre` - Theatres

For a complete list, see the [OpenTripMap documentation](https://opentripmap.com/product#kinds).

## Error Handling

### API Errors

| Error | Cause | Solution |
|-------|-------|----------|
| 401 Unauthorized | Invalid/missing API key | Verify API key in BuildConfig |
| 400 Bad Request | Invalid parameters (coords, radius) | Validate latitude (-90 to 90), longitude (-180 to 180) |
| 429 Too Many Requests | Rate limit exceeded | Implement request backoff strategy |
| 500 Server Error | Server error | Retry with exponential backoff |

### Common Issues

1. **No results returned**
   - Radius may be too small
   - No places of requested kinds in area
   - Location might be in unpopulated area

2. **Latitude/Longitude swapped**
   - Always use: longitude first in GeoJSON, but parameters are lat/lon
   - Verify: `lat` ∈ [-90, 90], `lon` ∈ [-180, 180]

3. **API Key issues**
   - Verify key is in `BuildConfig.OPEN_TRIP_MAP_API_KEY`
   - Check key is active on OpenTripMap console
   - Ensure Maps SDK enabled for your key

## Performance Optimization

### Radius Selection

| Radius | Impact | Use Case |
|--------|--------|----------|
| 200-500 m | Very fast, few results | Point of interest search |
| 1000-5000 m | Fast, moderate results | Local area exploration |
| 5000-20000 m | Medium, many results | Regional search |
| 20000+ m | Slow, large result set | City-wide search (use pagination) |

### Pagination

For large result sets, use `skip` parameter:

```kotlin
// First page
getNearbyPlaces(..., limit = 50, skip = 0)

// Second page
getNearbyPlaces(..., limit = 50, skip = 50)

// Third page
getNearbyPlaces(..., limit = 50, skip = 100)
```

## Integration with CityPulse

### MapsFragment Auto-Fetch

```kotlin
// When user location detected
if (!hasQueriedPlaces) {
    hasQueriedPlaces = true
    viewModel.loadNearbyPlaces(latitude, longitude)
}

// Displays on map as markers
displayNearbyPlaces(state.nearbyPlaces)
```

### Error Handling

```kotlin
// Display error container if fetch fails
if (state.errorMessage.isNotEmpty()) {
    showError(state.errorMessage)
}

// User can retry
viewModel.retryLoadNearbyPlaces(lat, lon)
```

## Testing the Endpoint

### Manual Test URL

```
https://api.opentripmap.com/0.1/places/radius?lat=51.5074&lon=-0.1278&radius=1000&limit=10&apikey=YOUR_KEY
```

### curl Example

```bash
curl "https://api.opentripmap.com/0.1/places/radius?lat=51.5074&lon=-0.1278&radius=1000&limit=10&apikey=YOUR_KEY"
```

### Android Studio Logcat

Enable logging in `HttpLoggingInterceptor`:

```
Level.BODY shows full request/response bodies
Level.HEADERS shows request/response headers
```

---

**Endpoint Status**: ✅ Verified and working  
**API Version**: 0.1  
**Response Format**: GeoJSON FeatureCollection  
**Last Updated**: May 24, 2026

