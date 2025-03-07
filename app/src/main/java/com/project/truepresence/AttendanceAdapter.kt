package com.project.truepresence

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible

class AttendanceAdapter(private val attendanceMap: Map<String, List<AttendanceLog>>) :
    RecyclerView.Adapter<AttendanceAdapter.MonthViewHolder>() {

    private val expandedMonths = mutableSetOf<String>()

    class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthText: TextView = itemView.findViewById(R.id.monthText)
        val expandIcon: ImageView = itemView.findViewById(R.id.expandIcon)
        val dayRecyclerView: RecyclerView = itemView.findViewById(R.id.dayRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_month_attendance, parent, false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val month = attendanceMap.keys.elementAt(position)
        val logs = attendanceMap[month] ?: emptyList()

        holder.monthText.text = month

        // Expand/Collapse logic with smooth animation
        val isExpanded = expandedMonths.contains(month)
        holder.dayRecyclerView.isVisible = isExpanded

        // Rotate expand icon with animation
        val rotationAngle = if (isExpanded) 180f else 0f
        ObjectAnimator.ofFloat(holder.expandIcon, "rotation", rotationAngle).start()

        // ✅ Pass formatted timestamps to DayAdapter
        holder.dayRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.dayRecyclerView.adapter = DayAdapter(logs.groupBy { it.getDay() })

        // Toggle expand/collapse on click
        holder.itemView.setOnClickListener {
            toggleMonthExpansion(position, month, holder)
        }
    }

    override fun getItemCount(): Int = attendanceMap.size

    private fun toggleMonthExpansion(position: Int, month: String, holder: MonthViewHolder) {
        val isExpanded = expandedMonths.contains(month)

        if (isExpanded) {
            expandedMonths.remove(month)
        } else {
            expandedMonths.add(month)
        }

        // Animate rotation
        val newRotation = if (isExpanded) 0f else 180f
        ObjectAnimator.ofFloat(holder.expandIcon, "rotation", newRotation).start()

        notifyItemChanged(position) // ✅ Efficient UI update
    }
}
