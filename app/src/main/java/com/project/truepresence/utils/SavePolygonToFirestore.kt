package com.project.truepresence.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object SavePolygonToFirestore {

    fun savePolygons() {
        val polygons = listOf(
            mapOf(
                "name" to "Office Area",
                "coordinates" to listOf(
                    mapOf("lat" to 22.285942970998065, "lng" to 73.3638506312654),
                    mapOf("lat" to 22.285759203870796, "lng" to 73.3638506312654),
                    mapOf("lat" to 22.285759203870796, "lng" to 73.36420706963452),
                    mapOf("lat" to 22.285942970998065, "lng" to 73.36420706963452),
                    mapOf("lat" to 22.285942970998065, "lng" to 73.3638506312654) // Closing point
                )
            )
        )

        val geoFenceRef = FirebaseFirestore.getInstance()
            .collection("geo_fence")
            .document("office_location")

        geoFenceRef.set(mapOf("polygons" to polygons))
            .addOnSuccessListener { Log.d("Firestore", "Geo-fence saved successfully") }
            .addOnFailureListener { e -> Log.e("Firestore", "Failed to save geo-fence", e) }
    }
}
