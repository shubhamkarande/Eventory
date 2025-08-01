package com.eventory.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

object LocationHelper {
    
    fun getCurrentLocation(
        context: Context,
        onLocationReceived: (latitude: Double, longitude: Double) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && 
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError("Location permission not granted")
            // Use default location (San Francisco) for demo
            onLocationReceived(37.7749, -122.4194)
            return
        }
        
        val fusedLocationClient: FusedLocationProviderClient = 
            LocationServices.getFusedLocationProviderClient(context)
        
        val cancellationTokenSource = CancellationTokenSource()
        
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                onLocationReceived(location.latitude, location.longitude)
            } else {
                // Use default location if current location is null
                onLocationReceived(37.7749, -122.4194)
            }
        }.addOnFailureListener { exception ->
            onError("Failed to get location: ${exception.message}")
            // Use default location on failure
            onLocationReceived(37.7749, -122.4194)
        }
    }
    
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000 // Convert to kilometers
    }
}