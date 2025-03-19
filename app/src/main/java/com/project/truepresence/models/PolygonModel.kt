package com.project.truepresence.models

import org.osmdroid.util.GeoPoint

data class PolygonModel(val coordinates: List<GeoPoint> = emptyList()) {

    companion object {
        fun fromFirestore(data: List<*>): PolygonModel {
            val geoPoints = data.mapNotNull { point ->
                (point as? Map<*, *>)?.let { map ->
                    val lat = map["lat"] as? Double
                    val lng = map["lng"] as? Double
                    if (lat != null && lng != null) GeoPoint(lat, lng) else null
                }
            }
            return PolygonModel(geoPoints)
        }
    }
}
