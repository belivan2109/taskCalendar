package ru.university.mytaskcalendar

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Данные прописаны прямо в коде — это временные задачи-заглушки.
        // Позже (в этапе 3) они будут заменены на данные из базы Room.
        val tasks = listOf(
            getString(R.string.task_1),
            getString(R.string.task_2),
            getString(R.string.task_3),
            getString(R.string.task_4),
            getString(R.string.task_5)
        )

        // Находим ListView в разметке по id
        val listView = findViewById<ListView>(R.id.listViewTasks)

        // Создаём адаптер: связывает список строк с ListView
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            tasks
        )

        // Привязываем адаптер к ListView
        listView.adapter = adapter
    }
}