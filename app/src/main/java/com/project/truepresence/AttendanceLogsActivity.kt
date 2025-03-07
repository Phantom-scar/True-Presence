package com.project.truepresence

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceLogsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val attendanceMap = mutableMapOf<String, MutableList<AttendanceLog>>() // Year-Month -> Logs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_logs)

        recyclerView = findViewById(R.id.attendanceRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        attendanceAdapter = AttendanceAdapter(attendanceMap)
        recyclerView.adapter = attendanceAdapter

        fetchAttendanceLogs()
    }

    private fun fetchAttendanceLogs() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance()
                .collection("attendance")
                .whereEqualTo("userId", user.uid)
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener { documents ->
                    val newAttendanceMap = mutableMapOf<String, MutableList<AttendanceLog>>()

                    for (document in documents) {
                        val timestamp = document.getLong("timestamp") ?: 0L
                        val formattedDate = formatToMonthYear(timestamp)
                        newAttendanceMap.getOrPut(formattedDate) { mutableListOf() }.add(AttendanceLog(timestamp))
                    }

                    updateAttendanceLogs(newAttendanceMap) // ✅ Efficient Update
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load logs", Toast.LENGTH_SHORT).show()
                    Log.e("AttendanceLogs", "Error fetching logs: ${e.message}")
                }
        }
    }

    private fun updateAttendanceLogs(newData: MutableMap<String, MutableList<AttendanceLog>>) {
        val diffCallback = AttendanceDiffCallback(attendanceMap, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        attendanceMap.clear()
        attendanceMap.putAll(newData)

        diffResult.dispatchUpdatesTo(attendanceAdapter) // ✅ Efficient list update
    }

    private fun formatToMonthYear(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault()) // Example: "January 2024"
        return sdf.format(Date(timestamp))
    }
}
