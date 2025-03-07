package com.project.truepresence

import androidx.recyclerview.widget.DiffUtil

class AttendanceDiffCallback(
    private val oldData: Map<String, MutableList<AttendanceLog>>,
    private val newData: Map<String, MutableList<AttendanceLog>>
) : DiffUtil.Callback() {

    private val oldKeys = oldData.keys.toList()
    private val newKeys = newData.keys.toList()

    override fun getOldListSize(): Int = oldKeys.size
    override fun getNewListSize(): Int = newKeys.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldKeys[oldItemPosition] == newKeys[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldKeys[oldItemPosition]] == newData[newKeys[newItemPosition]]
    }
}
