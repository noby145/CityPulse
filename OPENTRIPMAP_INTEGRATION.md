# OpenTripMap Integration Guide

This project now includes a complete Retrofit-based API service for OpenTripMap with Kotlin coroutines and MVVM architecture.

## Setup

### 1. Get Your API Key
- Visit [OpenTripMap](https://opentripmap.com/product)
- Sign up and get your free API key
- Replace `YOUR_OPEN_TRIP_MAP_API_KEY` in `app/build.gradle.kts` (buildConfigField)

### 2. Structure

```
data/
├── remote/
│   ├── OpenTripMapService.kt      # Retrofit service interface
│   ├── RetrofitClient.kt          # Retrofit client factory
│   └── model/
│       └── Place.kt               # API data models
├── repository/
│   └── PlacesRepository.kt        # Repository layer (handles API calls)
│
ui/
└── places/
    └── PlacesViewModel.kt         # MVVM ViewModel with LiveData
```

## Usage

### In a Fragment or Activity:

```kotlin
import androidx.fragment.app.viewModels
import com.example.citypulse.ui.places.PlacesViewModel

class YourFragment : Fragment() {
    private val placesViewModel: PlacesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the UI state
        placesViewModel.uiState.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                // Show loading indicator
            }
            if (state.places.isNotEmpty()) {
                // Update UI with places
                displayPlaces(state.places)
            }
            if (state.errorMessage.isNotEmpty()) {
                // Show error message
                showError(state.errorMessage)
            }
        }

        // Fetch nearby places (e.g., user's current location)
        placesViewModel.getNearbyPlaces(
            latitude = 37.7749,
            longitude = -122.4194,
            radius = 5000,  // 5km radius
            kinds = "natural,historic", // Optional filter
            limit = 20
        )
    }
}
```

### Data Models

The API returns a `List<Place>` with the following properties:
- `id`: Unique place ID (xid)
- `name`: Place name
- `latitude`: Latitude coordinate
- `longitude`: Longitude coordinate
- `kinds`: Place categories (e.g., "natural", "historic")
- `wikidata`: Wikipedia data ID (optional)

### Key Features

✅ **Coroutines**: All API calls are suspend functions for async/await pattern
✅ **Error Handling**: Built-in Result<T> error handling
✅ **OkHttp Logging**: Debug network requests with LoggingInterceptor
✅ **Gson Serialization**: Automatic JSON to Kotlin object conversion
✅ **MVVM**: Clean separation of concerns with ViewModel and Repository
✅ **Type-Safe**: Full Retrofit type-safety for API endpoints

### API Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| latitude | Double | - | Center latitude |
| longitude | Double | - | Center longitude |
| radius | Int | 5000 | Search radius in meters |
| kinds | String | "" | Comma-separated place types |
| limit | Int | 50 | Max results to return |

### Example Place Kinds
- `natural` - Natural features
- `historic` - Historic sites
- `museum` - Museums
- `monument` - Monuments
- `military` - Military sites
- `beach` - Beaches

For a complete list, see [OpenTripMap Documentation](https://opentripmap.com/product#api).

