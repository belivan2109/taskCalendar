package ru.university.mytaskcalendar.ui

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import ru.university.mytaskcalendar.R

data class CalendarDay(
    val dayNumber: Int?,
    val dateString: String?,
    val hasTasks: Boolean = false,
    val isSelected: Boolean = false,
    val isToday: Boolean = false
)

class CalendarAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, CalendarAdapter.DayViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CalendarDay>() {
            override fun areItemsTheSame(old: CalendarDay, new: CalendarDay): Boolean {
                if (old.dateString == null && new.dateString == null) return true
                return old.dateString == new.dateString
            }

            override fun areContentsTheSame(old: CalendarDay, new: CalendarDay): Boolean {
                return old == new
            }
        }
    }

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvDot: View = view.findViewById(R.id.tvDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = getItem(position)

        if (day.dayNumber == null) {
            holder.tvDay.text = ""
            holder.tvDay.setBackgroundColor(Color.TRANSPARENT)
            holder.tvDot.visibility = View.INVISIBLE
            holder.itemView.setOnClickListener(null)
            return
        }

        holder.tvDay.text = day.dayNumber.toString()

        val defaultTextColor = MaterialColors.getColor(holder.tvDay, android.R.attr.textColorPrimary)
        val selectedBgColor = MaterialColors.getColor(holder.tvDay, com.google.android.material.R.attr.colorPrimary)
        val selectedTextColor = MaterialColors.getColor(holder.tvDay, com.google.android.material.R.attr.colorOnPrimary)

        if (day.isSelected) {
            holder.tvDay.setBackgroundColor(selectedBgColor)
            holder.tvDay.setTextColor(selectedTextColor)
        } else {
            holder.tvDay.setBackgroundColor(Color.TRANSPARENT)
            holder.tvDay.setTextColor(defaultTextColor)
        }

        holder.tvDay.setTypeface(null, if (day.isToday) Typeface.BOLD else Typeface.NORMAL)

        holder.tvDot.visibility = if (day.hasTasks) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            onDayClick(day)
        }
    }
}