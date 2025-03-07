package com.project.truepresence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DayAdapter(private val dayLogs: Map<String, List<AttendanceLog>>) :
    RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    private val dayList = dayLogs.keys.toList()

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.attendanceDateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.attendanceTimeTextView) // ✅ New time view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_attendance, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = dayList[position]
        val logs = dayLogs[day] ?: emptyList()

        holder.dateTextView.text = day

        // ✅ Display formatted timestamps in a list
        val formattedTimes = logs.joinToString("\n") { it.getFormattedDate() }
        holder.timeTextView.text = formattedTimes
    }

    override fun getItemCount(): Int = dayList.size
}
