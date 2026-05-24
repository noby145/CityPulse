# Google Maps Marker Click Handling

The Maps Fragment now includes interactive marker click handling with custom info windows displaying place details.

## Features

✅ **Marker Click Detection** - Automatically shows info window on marker tap
✅ **Custom Info Window** - Displays place name, category, and formatted coordinates
✅ **Material 3 Styling** - Info window uses Material Design components
✅ **Nearby Places Support** - Can display multiple markers for OpenTripMap results

## Info Window Display

When you click a marker on the map, an info window appears showing:

1. **Title** (Place Name) - e.g., "Current location" or "Golden Gate Bridge"
2. **Category** (Kind) - e.g., "natural", "historic", "museum" 
3. **Coordinates** - Formatted as degrees with compass direction, e.g., "37.7749° N, 122.4194° W"

## Using Nearby Places Markers

To display nearby places from OpenTripMap API as markers on the map:

```kotlin
// In a Fragment or Activity that has access to MapsFragment
val mapsFragment = supportFragmentManager.findFragmentByTag("maps") as? MapsFragment
val placesToShow = listOf(/* Place objects from PlacesViewModel */)
mapsFragment?.displayNearbyPlaces(placesToShow)
```

Each nearby place will appear as a marker with:
- Position on the map (lat/lng)
- Place name as the title
- Category and coordinates in the info window

## Custom Info Window Layout

The info window is defined in `res/layout/map_info_window.xml` and uses:
- MaterialTextView for styling consistency
- Material 3 color scheme
- Hierarchical text appearance (title, subtitle, body)

**Customize by editing:**
- `map_info_window.xml` - Change layout and styling
- `MapInfoWindowAdapter.kt` - Change how data is parsed/displayed
- `MapsFragment.kt` - Update snippet formatting

## Marker Click Listener

The click listener is set in `MapsFragment.onMapReady()`:

```kotlin
map.setOnMarkerClickListener { marker ->
    marker.showInfoWindow()
    true  // Consume the event
}
```

This prevents default behavior and ensures the custom info window is always shown.

## Current Location Marker

The current user location appears as a marker at app startup with:
- Title: "Current location"
- Category: "Current Location"
- Coordinates: User's GPS position formatted with compass directions

## Example Integration with Places

To integrate with the `PlacesFragment` and show nearby places on the map:

```kotlin
// In PlacesViewModel observer
placesViewModel.uiState.observe(viewLifecycleOwner) { state ->
    mapsFragment.displayNearbyPlaces(state.places)
}
```

This will overlay all nearby places on top the map for a unified location browsing experience.

