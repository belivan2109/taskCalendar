package ru.university.mytaskcalendar.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.university.mytaskcalendar.Room.AppDatabase
import ru.university.mytaskcalendar.R
import ru.university.mytaskcalendar.Task
import ru.university.mytaskcalendar.TaskAdapter
import ru.university.mytaskcalendar.Repository.TaskRepository
import ru.university.mytaskcalendar.ViewModels.TaskViewModel
import ru.university.mytaskcalendar.ViewModels.TaskViewModelFactory
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.appcompat.app.AlertDialog
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            TaskRepository(AppDatabase.getDatabase(this).taskDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = TaskAdapter(
            onClick = { task ->
                val intent = Intent(this, AddEditTaskActivity::class.java)
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            },
            onSwipe = { task ->
                showDeleteConfirmation(task)
            }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,  // без перетаскивания
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT  // свайп влево и вправо
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val task = adapter.getTaskAt(position)


                AlertDialog.Builder(this@MainActivity)
                    .setTitle(R.string.delete_title)
                    .setMessage(getString(R.string.delete_message, task.title))
                    .setPositiveButton(R.string.delete_yes) { _, _ ->
                        viewModel.deleteTask(task)
                    }
                    .setNegativeButton(R.string.delete_no) { _, _ ->
                        adapter.notifyItemChanged(position)  // возвращение карточки на место
                    }
                    .setOnCancelListener {
                        adapter.notifyItemChanged(position)
                    }
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
        // Подписка на StateFlow из ViewModel
        lifecycleScope.launch {
            viewModel.allTasks.collectLatest { tasks ->
                adapter.submitList(tasks)
            }
        }

        // Автозаполнение базы при первом запуске (если пустая)
        lifecycleScope.launch {
            viewModel.allTasks.collectLatest { tasks ->
                if (tasks.isEmpty()) {
                    viewModel.addTask(
                        Task(
                            title = "Сделать модуль 1",
                            description = "Настроить Android Studio",
                            date = "2026-10-01",
                            time = "10:00",
                            isDone = true
                        )
                    )
                    viewModel.addTask(
                        Task(
                            title = "Разобрать структуру проекта",
                            description = "Понять, где код и ресурсы",
                            date = "2026-10-01",
                            time = "14:00"
                        )
                    )
                    viewModel.addTask(
                        Task(
                            title = "Изучить RecyclerView",
                            description = "Адаптер и ViewHolder",
                            date = "2026-10-02",
                            time = "11:00"
                        )
                    )
                    viewModel.addTask(
                        Task(
                            title = "Подключить Room",
                            description = "Entity, DAO, Database",
                            date = "2026-10-03",
                            time = "09:00"
                        )
                    )
                    viewModel.addTask(
                        Task(
                            title = "Перейти на MVVM",
                            description = "ViewModel и Repository",
                            date = "2026-10-04",
                            time = "16:00"
                        )
                    )
                }
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivity(intent)

        }
        findViewById<Button>(R.id.btnDebug).setOnClickListener {
            startActivity(Intent(this, DebugActivity::class.java))
        }
    }
    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, task.title))
            .setPositiveButton(R.string.delete_yes) { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton(R.string.delete_no, null)
            .show()
    }
}