package ru.university.mytaskcalendar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получаем DAO через синглтон AppDatabase
        val dao = AppDatabase.getDatabase(this).taskDao()

        // Создаём адаптер. По клику — открываем экран деталей и передаём id задачи.
        adapter = TaskAdapter { task ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("task_id", task.id)
            startActivity(intent)
        }

        // Настраиваем RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Подписка на Flow: при любом изменении данных в базе список обновляется автоматически
        lifecycleScope.launch {
            dao.getAllTasks().collectLatest { tasks ->
                adapter.submitList(tasks)
            }
        }

        // При первом запуске (если база пуста) — заполняем её стартовыми задачами
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val existing = dao.getAllTasks().first()
                if (existing.isEmpty()) {
                    dao.insert(Task(title = "Сделать модуль 1", description = "Настроить Android Studio и создать проект", date = "2026-10-01", time = "10:00", isDone = true))
                    dao.insert(Task(title = "Разобрать структуру проекта", description = "Понять, где код, где ресурсы", date = "2026-10-01", time = "14:00"))
                    dao.insert(Task(title = "Изучить RecyclerView", description = "Разобраться с адаптером и ViewHolder", date = "2026-10-02", time = "11:00"))
                    dao.insert(Task(title = "Подключить Room", description = "Entity, DAO, Database", date = "2026-10-03", time = "09:00"))
                    dao.insert(Task(title = "Проверить переходы", description = "Убедиться, что Intent работает", date = "2026-10-04", time = "16:00"))
                }
            }
        }

        // FAB — открывает экран добавления задачи
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        // Отладочная кнопка — открывает DebugActivity
        findViewById<Button>(R.id.btnDebug).setOnClickListener {
            startActivity(Intent(this, DebugActivity::class.java))
        }
    }
}