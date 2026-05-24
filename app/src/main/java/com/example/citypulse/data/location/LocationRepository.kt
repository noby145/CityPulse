package com.example.citypulse.data.location

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationRepository(application: Application) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onSuccess: (Location) -> Unit,
        onError: (String) -> Unit,
    ) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(location)
                } else {
                    requestFreshLocation(onSuccess, onError)
                }
            }
            .addOnFailureListener {
                requestFreshLocation(onSuccess, onError)
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(
        onSuccess: (Location) -> Unit,
        onError: (String) -> Unit,
    ) {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token,
        ).addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(location)
            } else {
                onError("Unable to determine your current location.")
            }
        }.addOnFailureListener { exception ->
            onError(exception.message ?: "Unable to determine your current location.")
        }
    }
}


