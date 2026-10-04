package ru.university.mytaskcalendar

import java.util.Date
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val date: String,     // "yyyy-MM-dd"
    val time: String,     // "HH:mm"
    val isDone: Boolean = false
)