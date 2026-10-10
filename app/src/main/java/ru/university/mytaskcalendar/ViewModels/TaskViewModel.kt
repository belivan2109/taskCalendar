package ru.university.mytaskcalendar.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.university.mytaskcalendar.Repository.TaskRepository
import ru.university.mytaskcalendar.Task

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(task: Task) {
        viewModelScope.launch { repository.insert(task) }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repository.update(task) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.delete(task) }
    }

    fun loadTask(taskId: Long, onLoaded: (Task?) -> Unit) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            onLoaded(task)
        }
    }
}