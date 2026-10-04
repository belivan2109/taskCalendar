package ru.university.mytaskcalendar

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DebugActivity : AppCompatActivity() {

    private val TAG = "DebugActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        val dao = AppDatabase.getDatabase(this).taskDao()

        findViewById<Button>(R.id.btnAddTestTasks).setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dao.insert(Task(title = "Тест 1", description = "Первая", date = "2026-10-03", time = "10:00"))
                    dao.insert(Task(title = "Тест 2", description = "Вторая", date = "2026-10-03", time = "12:00"))
                    dao.insert(Task(title = "Тест 3", description = "Третья", date = "2026-10-04", time = "09:00"))
                }
                Log.d(TAG, "3 задачи добавлены")
            }
        }

        findViewById<Button>(R.id.btnPrintTasks).setOnClickListener {
            lifecycleScope.launch {
                val tasks = withContext(Dispatchers.IO) {
                    dao.getAllTasks().first()
                }
                Log.d(TAG, "Всего задач: ${tasks.size}")
                tasks.forEach {
                    Log.d(TAG, "ID=${it.id}, title=${it.title}, date=${it.date}, time=${it.time}, isDone=${it.isDone}")
                }
            }
        }
    }
}