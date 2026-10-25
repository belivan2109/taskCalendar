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
import ru.university.mytaskcalendar.R

data class CalendarDay(
    val dayNumber: Int?,          // null для пустых ячеек
    val dateString: String?,      // "yyyy-MM-dd", null для пустых
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
                // Если обе ячейки пустые (без даты) — считаем их одинаковыми
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

        // Если ячейка пустая (сдвиг в начале месяца)
        if (day.dayNumber == null) {
            holder.tvDay.text = ""
            holder.tvDay.setBackgroundColor(Color.TRANSPARENT)
            holder.tvDot.visibility = View.INVISIBLE
            holder.itemView.setOnClickListener(null)
            return
        }

        // Устанавливаем число
        holder.tvDay.text = day.dayNumber.toString()

        // Настраиваем цвет фона и текста (выделение)
        if (day.isSelected) {
            // Выбранный день — фиолетовый фон, белый текст
            holder.tvDay.setBackgroundColor(Color.parseColor("#6200EE"))
            holder.tvDay.setTextColor(Color.WHITE)
        } else {
            // Обычный день — прозрачный фон, чёрный текст
            holder.tvDay.setBackgroundColor(Color.TRANSPARENT)
            holder.tvDay.setTextColor(Color.BLACK)
        }

        // Если это сегодняшний день — делаем текст жирным
        holder.tvDay.setTypeface(null, if (day.isToday) Typeface.BOLD else Typeface.NORMAL)

        // Показываем или скрываем точку (если есть задачи)
        holder.tvDot.visibility = if (day.hasTasks) View.VISIBLE else View.INVISIBLE

        // Обработчик клика по дню
        holder.itemView.setOnClickListener {
            onDayClick(day)
        }
    }
}