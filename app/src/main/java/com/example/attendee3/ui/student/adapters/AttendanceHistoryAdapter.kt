package com.example.attendee3.ui.student.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.attendee3.R
import com.example.attendee3.databinding.ItemAttendanceHistoryBinding
import com.example.attendee3.models.AttendanceRecord

class AttendanceHistoryAdapter(
    private val onItemClick: (AttendanceRecord) -> Unit
) : ListAdapter<AttendanceRecord, AttendanceHistoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemAttendanceHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: AttendanceRecord) {
            binding.tvHistoryCourseName.text =
                if (!record.courseName.isNullOrBlank()) record.courseName
                else record.courseCode ?: "Unknown Unit"

            val venue = if (!record.venue.isNullOrBlank()) " · ${record.venue}" else ""
            binding.tvHistoryLecturer.text = "${record.lecturerName ?: "Lecturer"}$venue"

            binding.tvHistoryDate.text = record.date ?: "—"
            binding.tvHistoryTime.text = record.time ?: "—"

            val statusClean = record.status?.trim()?.lowercase() ?: "verified"

            // Resolve status label and clay color tokens — no hardcoded hex values.
            // Each branch returns (displayText, textColorRes, backgroundColorRes).
            val (statusText, textColorRes, bgColorRes) = when (statusClean) {
                "verified", "present" -> Triple("PRESENT", R.color.clay_success,  R.color.clay_success_container)
                "late"                -> Triple("LATE",    R.color.clay_warning,  R.color.clay_warning_container)
                "flagged"             -> Triple("FLAGGED", R.color.clay_error,    R.color.clay_error_container)
                "missed", "absent"    -> Triple("MISSED",  R.color.clay_error,    R.color.clay_error_container)
                else                  -> Triple(
                    record.status?.uppercase() ?: "PRESENT",
                    R.color.clay_success,
                    R.color.clay_success_container
                )
            }

            val ctx = binding.root.context
            binding.tvHistoryStatus.text = statusText
            binding.tvHistoryStatus.setTextColor(ContextCompat.getColor(ctx, textColorRes))
            binding.tvHistoryStatus.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, bgColorRes))

            binding.root.setOnClickListener { onItemClick(record) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AttendanceRecord>() {
            override fun areItemsTheSame(old: AttendanceRecord, new: AttendanceRecord) =
                old.id == new.id
            override fun areContentsTheSame(old: AttendanceRecord, new: AttendanceRecord) =
                old == new
        }
    }
}
