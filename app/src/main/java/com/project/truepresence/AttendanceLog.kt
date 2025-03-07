package com.project.truepresence

import java.text.SimpleDateFormat
import java.util.*

data class AttendanceLog(val timestamp: Long) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getDay(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Example: "05 Jan 2024"
        return sdf.format(Date(timestamp))
    }
}
