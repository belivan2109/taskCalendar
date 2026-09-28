package ru.university.mytaskcalendar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import java.util.Date
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tasks = createTestTasks()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TaskAdapter(tasks) { task ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("task_id", task.id)
            startActivity(intent)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createTestTasks(): List<Task> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrow = cal.time

        cal.add(Calendar.DAY_OF_MONTH, 2)
        val inThreeDays = cal.time

        return listOf(
            Task(6, "Сверстать экран задачи", "Создать разметку XML для отображения деталей задачи", inThreeDays, false),
            Task(7, "Настроить ViewModel", "Подключить ViewModel и LiveData для хранения данных", inThreeDays, false),
            Task(8, "Реализовать Room", "Создать базу данных, Entity и DAO для сохранения задач", inThreeDays, false),
            Task(9, "Добавить навигацию", "Настроить Navigation Component или переходы между экранами", inThreeDays, false),
            Task(10, "Сделать добавление задач", "Реализовать диалог или экран для создания новой задачи", inThreeDays, false),
            Task(11, "Сделать удаление задач", "Добавить свайп или кнопку для удаления задачи из списка", inThreeDays, false),
            Task(12, "Обработать повороты экрана", "Сохранять состояние списка при повороте устройства", inThreeDays, false),
            Task(13, "Добавить уведомления", "Настроить WorkManager для напоминаний о задачах", inThreeDays, false),
            Task(14, "Написать unit-тесты", "Покрыть тестами логику ViewModel и базы данных", inThreeDays, false),
            Task(15, "Подготовить защиту проекта", "Написать README и подготовить презентацию", inThreeDays, false)
        )
    }
}