# 404 Error Troubleshooting - OpenTripMap API

If you're seeing "Error loading places" with a 404 Not Found error, use this guide to troubleshoot.

## Problem Diagnosis

### HTTP 404: Not Found
This error means the API endpoint URL is not found. Common causes:
1. **Invalid or missing API key** (most common)
2. API endpoint URL is incorrect
3. OpenTripMap API service is temporarily down

### HTTP 401/403: Unauthorized
Your API key is invalid or expired. Get a new one.

## Solutions

### Solution 1: Get a Valid OpenTripMap API Key ⭐ START HERE

The app currently uses a placeholder key that won't work.

**Steps**:
1. Go to https://opentripmap.com/product
2. Sign up for a free account (5000 free requests per day)
3. Get your API key from the dashboard
4. Copy the key (looks like: `a1b2c3d4e5f6...`)

### Solution 2: Set Your API Key

**Option A: Via Build Configuration (Recommended)**
1. Open `app/build.gradle.kts`
2. Find line with `buildConfigField("String", "OPEN_TRIP_MAP_API_KEY", ...)`
3. Replace the API key:
```kotlin
buildConfigField("String", "OPEN_TRIP_MAP_API_KEY", "\"YOUR_ACTUAL_KEY_HERE\"")
```
4. Rebuild the app

**Option B: Via local.properties (More Secure)**
1. Create or edit `local.properties` in project root:
```properties
OPEN_TRIP_MAP_API_KEY=YOUR_ACTUAL_KEY_HERE
```
2. Update `app/build.gradle.kts`:
```kotlin
val apiKey = project.findProperty("OPEN_TRIP_MAP_API_KEY")?.toString() ?: "YOUR_KEY"
buildConfigField("String", "OPEN_TRIP_MAP_API_KEY", "\"$apiKey\"")
```

### Solution 3: Verify the Endpoint

**Current Endpoint**:
```
GET https://api.opentripmap.com/0.1/places/radius
```

**Test in Browser** (replace YOUR_KEY):
```
https://api.opentripmap.com/0.1/places/radius?lat=51.5074&lon=-0.1278&radius=1000&apikey=YOUR_KEY&limit=10
```

If this URL works in your browser, the endpoint is correct.

### Solution 4: Test with curl

```bash
curl "https://api.opentripmap.com/0.1/places/radius?lat=51.5074&lon=-0.1278&radius=1000&apikey=YOUR_KEY&limit=10"
```

Expected response (if working):
```json
{
  "type": "FeatureCollection",
  "features": [
    { "type": "Feature", "id": "Q123456", ... }
  ]
}
```

## Detailed Debugging Steps

### Step 1: Check the API Key
```kotlin
// In Android Studio console/logcat, the app will show:
// "OpenTripMap API key not configured" - if key is missing/placeholder
// "API authentication failed" - if key is invalid
```

### Step 2: Monitor Network Requests
1. Enable logging in `RetrofitClient.kt` - it's already enabled at BODY level
2. Check Android Studio Logcat for HTTP logs showing exact request URL
3. Look for:
```
--> GET /0.1/places/radius?lat=...&lon=...&radius=...
```

### Step 3: Verify Location Parameters
Ensure location is valid:
- Latitude: -90 to 90
- Longitude: -180 to 180
- Radius: 200 to 50000 (meters)

Example log:
```
--> GET /0.1/places/radius?lat=37.7749&lon=-122.4194&radius=5000&apikey=YOUR_KEY...
<-- 404 Not Found (if API key invalid or endpoint wrong)
```

## Error Messages Guide

| Error | Cause | Fix |
|-------|-------|-----|
| "API endpoint not found (404)" | Wrong API key or bad endpoint | Check API key, verify endpoint URL |
| "API authentication failed" (401/403) | Invalid/expired API key | Get new key from opentripmap.com |
| "Rate limit exceeded" (429) | Too many requests | Wait before retrying |
| Empty list of places | Location has no nearby places | Try different location with larger radius |

## Common Issues

### Issue: Still Getting 404 After Adding Key

**Check**:
1. Did you rebuild the app after changing `build.gradle.kts`?
2. Is your API key format correct? (Should be alphanumeric string)
3. Is the key actually from OpenTripMap? (Not from Google Maps or other service)

**Solution**:
- Clean build: `./gradlew clean build`
- Uninstall app from device
- Rebuild and reinstall

### Issue: Getting Different Error (401, 429, timeout)

**401**: API key invalid
- Get new key from https://opentripmap.com

**429**: Rate limited
- Wait a few seconds
- Avoid multiple requests in quick succession

**Timeout**: Endpoint not responding
- Wait and retry
- Check internet connection

### Issue: Logcat Shows API Key as "YOUR_OPEN_TRIP_MAP_API_KEY"

This means the placeholder key is still in use.

**Fix**:
1. Open `app/build.gradle.kts`
2. Change THIS:
```kotlin
buildConfigField("String", "OPEN_TRIP_MAP_API_KEY", "\"YOUR_OPEN_TRIP_MAP_API_KEY\"")
```
3. To THIS (with real key):
```kotlin
buildConfigField("String", "OPEN_TRIP_MAP_API_KEY", "\"5ae2e3f221c38a28845f05b603e843f769c9ea5d51cc0ae09396f2ba\"")
```
4. Clean rebuild

## Next Steps

1. ✅ Get API key from https://opentripmap.com/product
2. ✅ Update `build.gradle.kts` with your key
3. ✅ Clean rebuild: `./gradlew clean build`
4. ✅ Reinstall app
5. ✅ Test: Grant location permission → Check map for "X nearby places found"

## Still Not Working?

**Enable detailed logging**:
1. In `RetrofitClient.kt`, logging is already at BODY level
2. Check Android Studio Logcat: `logcat | grep "http"`
3. Look for the exact request URL being sent
4. Try that URL in a browser with your API key
5. If browser fails, the problem is with the API key or endpoint

---

**Need Help?**
- OpenTripMap API Docs: https://opentripmap.com/product
- Forums: https://opentripmap.com/forum
- Email: support@opentripmap.com

**Last Updated**: May 24, 2026

