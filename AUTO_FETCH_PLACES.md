# Auto-Fetch Nearby Places

The MapsFragment now automatically fetches nearby places after the current user location is detected, with built-in protection against duplicate API calls during configuration changes (like screen rotation).

## How It Works

### 1. **Automatic Fetch Trigger**
When the user's current location is successfully loaded:
- The `MapsViewModel` automatically calls `loadNearbyPlaces()`
- Places are fetched within a 5km radius of the detected location
- Up to 50 nearby places are retrieved

### 2. **Duplicate Prevention**
To avoid duplicate API calls during configuration changes:
- A `hasQueriedPlaces` flag tracks whether we've already fetched places
- Once the flag is set to `true`, subsequent location re-detections do NOT trigger API calls
- Configuration changes (like screen rotation) preserve the ViewModel state, so no re-fetching occurs
- Fresh app launch will perform a new fetch

### 3. **LiveData State Management**
The `MapsUiState` includes:
- `nearbyPlaces: List<Place>` - The fetched places
- `placesLoading: Boolean` - Loading state during fetch
- Existing fields are preserved during state updates

### 4. **Marker Display**
Once places are loaded:
- Each place appears as a marker on the map
- Current location marker is preserved (not cleared)
- Nearby place markers show the place name with category and coordinates in the info window
- Users can click any marker to see details

## Implementation Details

### MapsViewModel Changes
```kotlin
private var hasQueriedPlaces = false  // Prevents duplicate fetches

fun loadCurrentLocation() {
    // ... fetch location ...
    if (!hasQueriedPlaces) {
        hasQueriedPlaces = true
        loadNearbyPlaces(latLng.latitude, latLng.longitude)
    }
}

private fun loadNearbyPlaces(latitude: Double, longitude: Double) {
    // Coroutine-based API fetch with silent error handling
}
```

### MapsFragment Changes
```kotlin
viewModel.uiState.observe(viewLifecycleOwner) { state ->
    // ... update current location ...
    
    // Display nearby places when loaded
    if (state.nearbyPlaces.isNotEmpty() && mapReady) {
        displayNearbyPlaces(state.nearbyPlaces)
    }
}
```

## Behavior Scenarios

### Scenario 1: Fresh App Launch
1. User grants location permission
2. GPS determines current location ✅
3. Nearby places API fetches automatically ✅
4. Markers appear on map with current location + nearby places

### Scenario 2: Screen Rotation
1. Config change occurs
2. ViewModel preserved (no state loss)
3. `hasQueriedPlaces` flag remains `true`
4. **No duplicate API call** ✅
5. Same markers remain on map

### Scenario 3: App Backgrounded/Resumed
1. Activity destroyed and recreated
2. **New ViewModel instance** (fresh state)
3. `hasQueriedPlaces` flag reset to `false`
4. New location fetch trigger → new API call ✅
5. Updated nearby places loaded

## Error Handling
- If nearby places fetch fails, the error is **silently ignored**
- Current location marker still displays
- `placesLoading` flag returns to `false`
- User can still interact with the map normally

## Future Enhancements
- [ ] Add a "Refresh" button to manually re-query nearby places
- [ ] Support filtering by place category (kinds)
- [ ] Add pagination for large result sets
- [ ] Cache results to reduce API calls on app resume

## Testing
To verify the auto-fetch works:
1. Grant location permission
2. Current location appears with a marker
3. Wait a few seconds for nearby places API call
4. Nearby place markers appear around your location
5. Rotate the screen → See same markers (no duplicate API call)

---

**API Used:** OpenTripMap Places API  
**Default Radius:** 5000 meters (5 km)  
**Default Limit:** 50 nearby places  
**Auto-Fetch:** Yes (after location detected)  
**Duplicate Prevention:** Yes (flag-based)

