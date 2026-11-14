package ru.university.mytaskcalendar.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.university.mytaskcalendar.R
import ru.university.mytaskcalendar.Repository.TaskRepository
import ru.university.mytaskcalendar.Room.AppDatabase
import ru.university.mytaskcalendar.Task
import ru.university.mytaskcalendar.TaskAdapter
import ru.university.mytaskcalendar.ViewModels.TaskViewModel
import ru.university.mytaskcalendar.ViewModels.TaskViewModelFactory
import ru.university.mytaskcalendar.receiver.AlarmScheduler
import java.util.Calendar
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(AppDatabase.getDatabase(this).taskDao()))
    }

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var tasksAdapter: TaskAdapter

    private var currentYear = 0
    private var currentMonth = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        findViewById<ImageButton>(R.id.btnGoToList).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        requestNotificationPermission()

        val cal = Calendar.getInstance()
        currentYear = cal.get(Calendar.YEAR)
        currentMonth = cal.get(Calendar.MONTH)

        calendarAdapter = CalendarAdapter { day ->
            if (day.dateString != null) {
                viewModel.setSelectedDate(day.dateString)
            }
        }
        val recyclerCalendar = findViewById<RecyclerView>(R.id.recyclerViewCalendar)
        recyclerCalendar.layoutManager = GridLayoutManager(this, 7)
        recyclerCalendar.adapter = calendarAdapter

        tasksAdapter = TaskAdapter(
            onClick = { task ->
                val intent = Intent(this, AddEditTaskActivity::class.java)
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            },
            onSwipe = { task ->
                AlarmScheduler.cancel(this, task)
                viewModel.deleteTask(task)
            }
        )
        val recyclerTasks = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        recyclerTasks.layoutManager = LinearLayoutManager(this)
        recyclerTasks.adapter = tasksAdapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val task = tasksAdapter.getTaskAt(position)
                showDeleteConfirmation(task, position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerTasks)

        findViewById<ImageButton>(R.id.btnPrevMonth).setOnClickListener {
            if (currentMonth == 0) {
                currentMonth = 11
                currentYear--
            } else {
                currentMonth--
            }
            refreshCalendar()
        }

        findViewById<ImageButton>(R.id.btnNextMonth).setOnClickListener {
            if (currentMonth == 11) {
                currentMonth = 0
                currentYear++
            } else {
                currentMonth++
            }
            refreshCalendar()
        }

        findViewById<FloatingActionButton>(R.id.fabAddOnDate).setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            intent.putExtra("preset_date", viewModel.selectedDate.value)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            showThemeDialog()
        }

        lifecycleScope.launch {
            viewModel.tasksForSelectedDate.collectLatest { tasks ->
                tasksAdapter.submitList(tasks)

                val tvEmpty = findViewById<TextView>(R.id.tvEmptyCalendarState)
                if (tasks.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    tvEmpty.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.datesWithTasks.collectLatest {
                rebuildCalendarGrid()
            }
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collectLatest { date ->
                findViewById<TextView>(R.id.tvSelectedDate).text =
                    getString(R.string.tasks_for_date, date)
                rebuildCalendarGrid()
            }
        }

        refreshCalendar()
    }
    private fun showThemeDialog() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val currentMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        val modes = arrayOf("Как в системе", "Светлая", "Тёмная")
        val modeValues = intArrayOf(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES
        )
        val checkedItem = modeValues.indexOf(currentMode)

        AlertDialog.Builder(this)
            .setTitle("Тема оформления")
            .setSingleChoiceItems(modes, checkedItem) { dialog, which ->
                val selectedMode = modeValues[which]
                prefs.edit().putInt("theme_mode", selectedMode).apply()
                AppCompatDelegate.setDefaultNightMode(selectedMode)
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (currentYear != 0) {
            refreshCalendar()
        }
    }

    private fun showDeleteConfirmation(task: Task, position: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, task.title))
            .setPositiveButton(R.string.delete_yes) { _, _ ->
                AlarmScheduler.cancel(this, task)
                viewModel.deleteTask(task)
            }
            .setNegativeButton(R.string.delete_no) { _, _ ->
                tasksAdapter.notifyItemChanged(position)
            }
            .setOnCancelListener {
                tasksAdapter.notifyItemChanged(position)
            }
            .show()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun refreshCalendar() {
        val cal = Calendar.getInstance()
        cal.set(currentYear, currentMonth, 1)

        val monthName = cal.getDisplayName(
            Calendar.MONTH, Calendar.LONG, Locale.getDefault()
        )
        findViewById<TextView>(R.id.tvMonthYear).text = "$monthName $currentYear"

        viewModel.loadDatesForMonth(currentYear, currentMonth)
    }

    private fun rebuildCalendarGrid() {
        val cal = Calendar.getInstance()
        cal.set(currentYear, currentMonth, 1)

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val emptyCells = when (firstDayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }

        val today = SimpleToday()
        val selected = viewModel.selectedDate.value
        val datesWithTasks = viewModel.datesWithTasks.value

        val grid = mutableListOf<CalendarDay>()

        repeat(emptyCells) {
            grid.add(CalendarDay(null, null))
        }

        for (day in 1..daysInMonth) {
            val dateString = String.format(
                Locale.getDefault(), "%04d-%02d-%02d",
                currentYear, currentMonth + 1, day
            )
            grid.add(
                CalendarDay(
                    dayNumber = day,
                    dateString = dateString,
                    hasTasks = datesWithTasks.contains(dateString),
                    isSelected = dateString == selected,
                    isToday = dateString == today
                )
            )
        }

        calendarAdapter.submitList(grid)
    }

    private fun SimpleToday(): String {
        val cal = Calendar.getInstance()
        return String.format(
            Locale.getDefault(), "%04d-%02d-%02d",
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }
}