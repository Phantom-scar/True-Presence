package com.project.truepresence

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Feature Cards
        val profileFeature = findViewById<LinearLayout>(R.id.profileFeature)
        val facialAttendanceFeature = findViewById<LinearLayout>(R.id.facialAttendanceFeature)
        val geofenceFeature = findViewById<LinearLayout>(R.id.geofenceFeature)
        val attendanceLogsFeature = findViewById<LinearLayout>(R.id.attendanceLogsFeature)
        val otherFeature = findViewById<LinearLayout>(R.id.otherFeature)

        // Apply Click Animations
        applyClickEffect(profileFeature)
        applyClickEffect(facialAttendanceFeature)
        applyClickEffect(geofenceFeature)
        applyClickEffect(attendanceLogsFeature)
        applyClickEffect(otherFeature)

        // Navigation
        profileFeature.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        facialAttendanceFeature.setOnClickListener {
            startActivity(Intent(this, FacialAttendanceActivity::class.java))
        }

        geofenceFeature.setOnClickListener {
            startActivity(Intent(this, GeofenceActivity::class.java))
        }

        attendanceLogsFeature.setOnClickListener {
            startActivity(Intent(this, AttendanceLogsActivity::class.java))
        }

        otherFeature.setOnClickListener {
            startActivity(Intent(this, OtherFeaturesActivity::class.java))
        }
    }

    private fun applyClickEffect(view: LinearLayout) {
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        view.setOnClickListener {
            it.startAnimation(bounceAnimation)
        }
    }
}
