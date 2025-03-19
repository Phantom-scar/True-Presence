package com.project.truepresence

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.truepresence.utils.LocationHelper
import com.project.truepresence.utils.GeoFenceHelper
import com.project.truepresence.utils.SavePolygonToFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

class GeofenceActivity : AppCompatActivity() {

    private lateinit var osmMapView: MapView
    private lateinit var geoFenceStatus: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofence)

        // Initialize UI elements
        osmMapView = findViewById(R.id.osmMapView)
        geoFenceStatus = findViewById(R.id.geoFenceStatus)
        distanceTextView = findViewById(R.id.distanceTextView)
        refreshButton = findViewById(R.id.refreshLocationButton)

        // Configure OSM (OpenStreetMap)
        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))
        osmMapView.setTileSource(TileSourceFactory.MAPNIK)
        osmMapView.setMultiTouchControls(true)

        // Save polygons and fetch location on start
        SavePolygonToFirestore.savePolygons()
        fetchLocation()

        // Refresh location on button click
        refreshButton.setOnClickListener {
            fetchLocation()
        }
    }

    private fun fetchLocation() {
        geoFenceStatus.text = getString(R.string.geo_checking)

        LocationHelper.getCurrentLocation(this) { userLocation ->
            if (userLocation != null) {
                updateMap(userLocation)
                checkGeoFence(userLocation)
            } else {
                Toast.makeText(this, getString(R.string.geo_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkGeoFence(userLocation: GeoPoint) {
        GeoFenceHelper.checkIfUserInsideGeoFence(this, userLocation) { inside, distance ->
            if (inside) {
                geoFenceStatus.text = getString(R.string.geo_inside)
                distanceTextView.text = ""
            } else {
                geoFenceStatus.text = getString(R.string.geo_outside)
                distanceTextView.text = getString(R.string.geo_distance, distance ?: 0.0)
            }
        }
    }

    private fun updateMap(userLocation: GeoPoint) {
        osmMapView.overlays.clear()

        // Add user marker
        val userMarker = Marker(osmMapView).apply {
            position = userLocation
            title = "Your Location"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        osmMapView.overlays.add(userMarker)

        // Move camera to user location
        osmMapView.controller.setCenter(userLocation)
        osmMapView.controller.setZoom(18.0)

        // Draw stored polygons on map
        drawGeoFencePolygons()
    }

    private fun drawGeoFencePolygons() {
        val geoFencePolygons = listOf(
            listOf(
                GeoPoint(22.285942970998065, 73.3638506312654),
                GeoPoint(22.285759203870796, 73.3638506312654),
                GeoPoint(22.285759203870796, 73.36420706963452),
                GeoPoint(22.285942970998065, 73.36420706963452)
            ),
            listOf(
                GeoPoint(22.2852449899749, 73.3632344483427),
                GeoPoint(22.284853682539463, 73.3632344483427),
                GeoPoint(22.284853682539463, 73.36386344581669),
                GeoPoint(22.2852449899749, 73.36386344581669)
            )
        )

        for (polygonPoints in geoFencePolygons) {
            val polygon = Polygon().apply {
                points = polygonPoints
                outlinePaint.color = 0xFF00FF00.toInt() // ✅ Green border
                outlinePaint.strokeWidth = 4f
                fillPaint.color = 0x3300FF00 // ✅ Transparent green fill
            }
            osmMapView.overlays.add(polygon)
        }

        osmMapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        osmMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        osmMapView.onPause()
    }
}
