package com.project.truepresence

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.truepresence.adapters.FeatureAdapter  // ✅ Import FeatureAdapter
import com.project.truepresence.models.FeatureItem      // ✅ Import FeatureItem

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // RecyclerView for Feature Cards
        val featureRecyclerView = findViewById<RecyclerView>(R.id.featureRecyclerView)
        featureRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        featureRecyclerView.adapter = FeatureAdapter(this, getFeatureList())  // ✅ Works now
    }

    private fun getFeatureList(): List<FeatureItem> {
        return listOf(
            FeatureItem(R.drawable.ic_profile, R.string.profile1, ProfileActivity::class.java),
            FeatureItem(R.drawable.ic_face_scan, R.string.facial_attendance, FacialAttendanceActivity::class.java),
            FeatureItem(R.drawable.ic_location, R.string.geo_fence, GeofenceActivity::class.java),
            FeatureItem(R.drawable.ic_logs, R.string.attendance_logs, AttendanceLogsActivity::class.java),
            FeatureItem(R.drawable.ic_other, R.string.more_features, OtherFeaturesActivity::class.java)
        )
    }
}
