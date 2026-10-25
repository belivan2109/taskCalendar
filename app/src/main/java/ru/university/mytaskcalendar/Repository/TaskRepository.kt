package ru.university.mytaskcalendar.Repository

import kotlinx.coroutines.flow.Flow
import ru.university.mytaskcalendar.Task
import ru.university.mytaskcalendar.Room.TaskDao

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksByDate(date: String): Flow<List<Task>> = taskDao.getTasksByDate(date)

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun insert(task: Task): Long = taskDao.insert(task)

    suspend fun update(task: Task) = taskDao.update(task)

    suspend fun delete(task: Task) = taskDao.delete(task)

    suspend fun getDatesWithTasksForMonth(monthPrefix: String): List<String> =
        taskDao.getDatesWithTasksForMonth(monthPrefix)

}