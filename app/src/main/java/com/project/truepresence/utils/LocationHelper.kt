package com.project.truepresence.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.util.GeoPoint

object LocationHelper {

    fun getCurrentLocation(context: Context, callback: (GeoPoint?) -> Unit) {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LocationHelper", "Location permission not granted")
            callback(null)
            return
        }

        // ✅ Set Package Name Explicitly (Fix SecurityException)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    callback(GeoPoint(location.latitude, location.longitude))
                } else {
                    Log.e("LocationHelper", "Failed to get location")
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationHelper", "Error getting location: ${e.message}")
                callback(null)
            }
    }
}
