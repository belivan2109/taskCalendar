package ru.university.mytaskcalendar.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.AdapterView
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private var allTasksList: List<Task> = emptyList()
    private var currentQuery = ""
    private var currentSortMode = 0 // 0 - по дате, 1 - по алфавиту, 2 - невыполненные

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(AppDatabase.getDatabase(this).taskDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val tvEmptyState = findViewById<TextView>(R.id.tvEmptyState)

        adapter = TaskAdapter(
            onClick = { task ->
                val intent = Intent(this, AddEditTaskActivity::class.java)
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            },
            onSwipe = {}
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        val spinner = findViewById<Spinner>(R.id.spinnerSort)
        val sortOptions = arrayOf("По дате", "По алфавиту", "Сначала невыполненные")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSortMode = position
                applyFilterAndSort()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                applyFilterAndSort()
                return true
            }
        })

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val task = adapter.getTaskAt(position)
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(R.string.delete_title)
                    .setMessage(getString(R.string.delete_message, task.title))
                    .setPositiveButton(R.string.delete_yes) { _, _ ->
                        AlarmScheduler.cancel(this@MainActivity, task)
                        viewModel.deleteTask(task)
                    }
                    .setNegativeButton(R.string.delete_no) { _, _ -> adapter.notifyItemChanged(position) }
                    .setOnCancelListener { adapter.notifyItemChanged(position) }
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        lifecycleScope.launch {
            viewModel.allTasks.collectLatest { tasks ->
                allTasksList = tasks
                applyFilterAndSort()

                if (tasks.isEmpty()) {
                    tvEmptyState.text = "У вас пока нет задач"
                    tvEmptyState.visibility = View.VISIBLE
                } else {
                    tvEmptyState.visibility = View.GONE
                }
            }
        }

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddEditTaskActivity::class.java))
        }
    }

    private fun applyFilterAndSort() {
        var result = allTasksList

        // 1. Фильтрация по поиску (п. 38)
        if (currentQuery.isNotEmpty()) {
            result = result.filter { it.title.contains(currentQuery, ignoreCase = true) }
        }

        result = when (currentSortMode) {
            0 -> result.sortedWith(compareBy({ it.date }, { it.time })) // По дате и времени
            1 -> result.sortedBy { it.title.lowercase() }               // По алфавиту
            2 -> result.sortedWith(compareBy({ it.isDone }, { it.date }, { it.time })) // Сначала невыполненные
            else -> result
        }

        adapter.submitList(result)

        val tvEmptyState = findViewById<TextView>(R.id.tvEmptyState)
        if (result.isEmpty() && currentQuery.isNotEmpty()) {
            tvEmptyState.text = "По запросу ничего не найдено"
            tvEmptyState.visibility = View.VISIBLE
        } else if (result.isNotEmpty()) {
            tvEmptyState.visibility = View.GONE
        }
    }
}