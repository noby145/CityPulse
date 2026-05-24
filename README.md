# CityPulse

CityPulse now includes a Google Maps-based fragment built with MVVM, Material 3, and runtime location permission handling.

## Setup

1. Replace `YOUR_GOOGLE_MAPS_API_KEY` in `app/src/main/res/values/strings.xml` with your real Google Maps API key.
2. Make sure the Maps SDK for Android is enabled for that key in the Google Cloud console.
3. Run the app and grant `ACCESS_FINE_LOCATION` when prompted.

## What it shows

- A Google Map hosted inside a Fragment
- A runtime permission request for fine location
- A marker and camera movement to the user's current location
- A Material 3 status card with the latest location message

