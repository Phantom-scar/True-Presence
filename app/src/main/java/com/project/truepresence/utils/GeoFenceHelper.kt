package com.project.truepresence.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.project.truepresence.models.PolygonModel  // ✅ Import PolygonModel
import org.osmdroid.util.GeoPoint
import kotlin.math.*

object GeoFenceHelper {

    fun checkIfUserInsideGeoFence(
        context: Context,
        userLocation: GeoPoint,
        callback: (Boolean, Double?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("geo_fence").document("office_location").get()
            .addOnSuccessListener { document ->
                val polygonList = document.get("polygons") as? List<*> ?: return@addOnSuccessListener // ✅ Safe Casting

                var inside = false
                var minDistance: Double? = null

                for (polygonData in polygonList) {
                    val polygonMap = polygonData as? Map<*, *> ?: continue // ✅ Safe Casting

                    val polygonPoints = polygonMap["coordinates"] as? List<*> ?: continue // ✅ Safe Casting

                    val validPoints = polygonPoints.mapNotNull {
                        (it as? Map<*, *>)?.let { point ->
                            val lat = point["lat"] as? Double
                            val lng = point["lng"] as? Double
                            if (lat != null && lng != null) GeoPoint(lat, lng) else null
                        }
                    }

                    if (validPoints.isEmpty()) continue // ✅ Skip if no valid points

                    val polygon = PolygonModel(validPoints).coordinates

                    if (isPointInsidePolygon(userLocation, polygon)) {
                        inside = true
                        minDistance = 0.0
                        break
                    } else {
                        val distance = calculateDistanceToPolygon(userLocation, polygon)
                        minDistance = minDistance?.let { min(it, distance) } ?: distance
                    }
                }

                val message = if (inside) {
                    "✅ Inside Geo-Fence"
                } else {
                    "🚫 Outside Geo-Fence. Nearest Point: ${"%.2f".format(minDistance ?: 0.0)} meters away"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                callback(inside, minDistance)
            }
            .addOnFailureListener { e ->
                Log.e("GeoFenceHelper", "Failed to fetch polygon: ${e.message}")
                callback(false, null)
            }
    }

    // ✅ Fix: Check if the user is inside the polygon
    private fun isPointInsidePolygon(point: GeoPoint, polygon: List<GeoPoint>): Boolean {
        var intersections = 0
        val x = point.longitude
        val y = point.latitude

        for (i in polygon.indices) {
            val p1 = polygon[i]
            val p2 = polygon[(i + 1) % polygon.size]

            if (y > minOf(p1.latitude, p2.latitude) && y <= maxOf(p1.latitude, p2.latitude) &&
                x <= maxOf(p1.longitude, p2.longitude)
            ) {
                val xinters = (y - p1.latitude) * (p2.longitude - p1.longitude) /
                        (p2.latitude - p1.latitude) + p1.longitude
                if (xinters > x) {
                    intersections++
                }
            }
        }
        return intersections % 2 == 1
    }

    // ✅ Fix: Calculate distance to the closest polygon point
    private fun calculateDistanceToPolygon(userLocation: GeoPoint, polygon: List<GeoPoint>): Double {
        return polygon.minOfOrNull { haversineDistance(userLocation, it) } ?: Double.MAX_VALUE
    }

    // ✅ Fix: Haversine formula to calculate distance between two points
    private fun haversineDistance(p1: GeoPoint, p2: GeoPoint): Double {
        val earthRadius = 6371000.0 // Radius of Earth in meters
        val lat1 = Math.toRadians(p1.latitude)
        val lon1 = Math.toRadians(p1.longitude)
        val lat2 = Math.toRadians(p2.latitude)
        val lon2 = Math.toRadians(p2.longitude)

        val dlat = lat2 - lat1
        val dlon = lon2 - lon1

        val a = sin(dlat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c // Distance in meters
    }
}
