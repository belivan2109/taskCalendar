package ru.university.mytaskcalendar.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.university.mytaskcalendar.Repository.TaskRepository
import ru.university.mytaskcalendar.Task
<<<<<<< HEAD
=======
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
>>>>>>> 4d14543 (6 stage is done)

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow(getTodayDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val tasksForSelectedDate: StateFlow<List<Task>> = _selectedDate
        .flatMapLatest { date -> repository.getTasksByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _datesWithTasks = MutableStateFlow<Set<String>>(emptySet())
    val datesWithTasks: StateFlow<Set<String>> = _datesWithTasks.asStateFlow()

    // Храним префикс текущего открытого месяца в календаре (например, "2026-10")
    private var currentMonthPrefix: String? = null

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun loadDatesForMonth(year: Int, month: Int) {
        val prefix = String.format(Locale.getDefault(), "%04d-%02d", year, month + 1)
        currentMonthPrefix = prefix
        viewModelScope.launch {
            val dates = repository.getDatesWithTasksForMonth(prefix)
            _datesWithTasks.value = dates.toSet()
        }
    }

    // Вспомогательный метод, чтобы обновить точки после любых действий
    private fun refreshDatesIfNeeded() {
        currentMonthPrefix?.let { prefix ->
            viewModelScope.launch {
                val dates = repository.getDatesWithTasksForMonth(prefix)
                _datesWithTasks.value = dates.toSet()
            }
        }
    }

    fun addTask(task: Task) {
<<<<<<< HEAD
        viewModelScope.launch { repository.insert(task) }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repository.update(task) }
=======
        viewModelScope.launch {
            repository.insert(task)
            refreshDatesIfNeeded()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
            refreshDatesIfNeeded()
        }
>>>>>>> 4d14543 (6 stage is done)
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.delete(task) }
    }

    fun loadTask(taskId: Long, onLoaded: (Task?) -> Unit) {
        viewModelScope.launch {
<<<<<<< HEAD
            val task = repository.getTaskById(taskId)
            onLoaded(task)
=======
            repository.delete(task)
            refreshDatesIfNeeded()
>>>>>>> 4d14543 (6 stage is done)
        }
    }

    fun loadTask(taskId: Long, onLoaded: (Task?) -> Unit) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            onLoaded(task)
        }
    }

    companion object {
        fun getTodayDate(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
    }
}