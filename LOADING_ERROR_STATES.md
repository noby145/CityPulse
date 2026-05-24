# Loading & Error State UI - Material 3

The MapsFragment now displays comprehensive loading and error states using Material 3 components, providing clear visual feedback to users as nearby places are fetched.

## UI Components Added

### 1. **Circular Progress Indicator**
- **Location**: Top-right of the map
- **Visibility**: Shows when `placesLoading = true`
- **Material 3 Style**: Uses `CircularProgressIndicator` with automatic theme colors
- **Indeterminate**: Continuous loading animation while fetching

### 2. **Error Container Card**
- **Location**: Top of the map (below progress indicator)
- **Visibility**: Shows when `errorMessage` is not empty
- **Material 3 Colors**: Uses error container colors (`colorErrorContainer`, `colorOnErrorContainer`)
- **Contents**:
  - Error title: "Error Loading Places"
  - Error message: Details of what went wrong
  - Action buttons: Retry and Dismiss

### 3. **Places Count Indicator**
- **Location**: In the location status card at bottom
- **Format**: "X nearby places found"
- **Visibility**: Shows only when places are successfully loaded
- **Hidden**: When no places are available

### 4. **Location Status Card** (Enhanced)
- **Now contains**: Location info + Places count
- **Material 3 Style**: Rounded corners, elevation, outline

## State Management

### Loading States
```kotlin
MapsUiState(
    placesLoading = true   // Shows progress indicator
    errorMessage = ""      // Hides error container
)
```

### Error States
```kotlin
MapsUiState(
    placesLoading = false
    errorMessage = "Network error"  // Shows error container
    nearbyPlaces = emptyList()
)
```

### Success States
```kotlin
MapsUiState(
    placesLoading = false
    errorMessage = ""              // Hides error & progress
    nearbyPlaces = listOf(...)     // Shows places count
)
```

## User Interactions

### Retry Button
- **Visible**: When error occurs
- **Action**: Re-fetches nearby places from current location
- **Behavior**: Clears error message, starts loading again

### Dismiss Button
- **Visible**: When error occurs
- **Action**: Hides the error container without retrying
- **Behavior**: Users can still interact with the map

## Implementation Details

### Fragment Updates
```kotlin
// Observe loading state
binding.placesLoadingIndicator.visibility = 
    if (state.placesLoading) View.VISIBLE else View.GONE

// Observe error state
if (state.errorMessage.isNotEmpty()) {
    showError(state.errorMessage)
} else {
    hideError()
}

// Display places count
val placesCount = String.format(
    getString(R.string.places_count_format),
    state.nearbyPlaces.size
)
binding.placesCount.text = placesCount
```

### ViewModel Error Handling
```kotlin
result.onFailure { exception ->
    val errorMsg = exception.message 
        ?: getString(R.string.unable_to_load_places)
    
    _uiState.postValue(
        MapsUiState(
            placesLoading = false,
            errorMessage = errorMsg,  // Populated with actual error
        )
    )
}
```

## Material 3 Color Theming

### Error Container
Uses Material 3 error container colors:
- **Background**: `colorErrorContainer` (error-tinted background)
- **Text**: `colorOnErrorContainer` (contrasting text)
- **Buttons**: Inherit text button styling

### Progress Indicator
- **Color**: Automatically uses primary color from Material 3 theme
- **Respects**: Light/dark mode from Material 3 DayNight theme

### Location Card
- **Outline**: `colorOutlineVariant`
- **Elevation**: Material 3 standard elevation

## Visual Behavior Timeline

### Scenario 1: Fetch in Progress
1. User grants location permission
2. Location detected ✅
3. Progress indicator appears ⟳
4. Nearby places API call starts

### Scenario 2: Fetch Completes Successfully
1. Progress indicator disappears ✓
2. Error container remains hidden (if no error) ✓
3. Places count displayed: "22 nearby places found"
4. Markers appear on map

### Scenario 3: Fetch Fails
1. Progress indicator disappears ✓
2. Error container appears ✓
3. Error message shows (e.g., "Network timeout")
4. Retry button available

### Scenario 4: User Retries After Error
1. User taps Retry button
2. Error container dismissed
3. Progress indicator appears ⟳
4. API fetch retried

## Testing Checklist

- [ ] See progress indicator when places are loading
- [ ] See error container with message when API fails
- [ ] Click Retry button to re-fetch places
- [ ] Click Dismiss to hide error without retrying
- [ ] See places count when fetch succeeds
- [ ] Verify Material 3 colors match app theme
- [ ] Test in light and dark modes
- [ ] Screen rotation preserves error/loading state

## Strings Used

| String ID | Usage |
|-----------|-------|
| `error_loading_places` | Error title in container |
| `retry` | Retry button label |
| `dismiss` | Dismiss button label |
| `places_count_format` | "X nearby places found" |
| `unable_to_load_places` | Default error message |

## Future Enhancements

- [ ] Add different error types (network vs API errors)
- [ ] Add offline detection indicator
- [ ] Add retry count limit
- [ ] Add expandable error details
- [ ] Add analytics for error tracking
- [ ] Add haptic feedback on error/success

---

**Build Status**: ✅ BUILD SUCCESSFUL  
**Material 3 Components Used**: ✅ CircularProgressIndicator, MaterialCardView, MaterialTextView, MaterialButton  
**Dark Mode Support**: ✅ Yes (Material 3 DayNight theme)

