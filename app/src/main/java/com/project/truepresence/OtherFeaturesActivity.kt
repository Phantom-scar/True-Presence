package com.project.truepresence

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions  // ✅ FIXED: Import SetOptions
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.MapEventsOverlay  // ✅ FIXED: Add Map Event Listener
import org.osmdroid.events.MapEventsReceiver  // ✅ FIXED: Import Correct Interface

class OtherFeaturesActivity : AppCompatActivity(), MapEventsReceiver {  // ✅ Implement MapEventsReceiver

    private lateinit var mapView: MapView
    private lateinit var saveGeofenceButton: Button
    private val geofencePoints = mutableListOf<GeoPoint>()
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_features)

        // 🔹 Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // 🔹 Map setup
        mapView = findViewById(R.id.osmMapView)
        saveGeofenceButton = findViewById(R.id.saveGeofenceButton)

        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(18.0)

        requestLocationPermission()

        // 🗺 Add event listener to the map
        val eventsOverlay = MapEventsOverlay(this)  // ✅ FIXED: Use MapEventsOverlay correctly
        mapView.overlays.add(eventsOverlay)

        // 💾 Save geofence inside "office_location"
        saveGeofenceButton.setOnClickListener {
            if (geofencePoints.size < 3) {
                Toast.makeText(this, "Add at least 3 points!", Toast.LENGTH_SHORT).show()
            } else {
                saveGeofenceToFirestore()
            }
        }
    }

    private fun requestLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Location permission is required!", Toast.LENGTH_LONG).show()
            }
        }
        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun addMarker(point: GeoPoint) {
        val marker = Marker(mapView).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    private fun drawGeofence() {
        val polygon = Polygon().apply {
            points = geofencePoints
            fillPaint.color = 0x3300FF00  // ✅ Transparent green fill
            outlinePaint.color = 0xFF00FF00.toInt()  // ✅ Solid green border
            outlinePaint.strokeWidth = 3f
        }
        mapView.overlays.add(polygon)
        mapView.invalidate()
    }

    private fun saveGeofenceToFirestore() {
        val geofenceData = mapOf(
            "custom_geofence" to geofencePoints.map { mapOf("lat" to it.latitude, "lng" to it.longitude) }
        )

        // 🔥 Save inside "office_location" without overwriting other data
        firestore.collection("geo_fence").document("office_location")
            .set(geofenceData, SetOptions.merge())  // ✅ FIXED: Correct SetOptions usage
            .addOnSuccessListener {
                Toast.makeText(this, "Geofence saved inside 'office_location'!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save geofence: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ FIXED: Implementing MapEventsReceiver properly
    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        p?.let {
            geofencePoints.add(it)
            addMarker(it)
            drawGeofence()
        }
        return true
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        return false
    }
}
