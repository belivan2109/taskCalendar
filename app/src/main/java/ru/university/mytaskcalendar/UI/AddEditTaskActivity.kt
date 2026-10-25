<<<<<<< HEAD
package ru.university.mytaskcalendar.UI
=======
package ru.university.mytaskcalendar.ui
>>>>>>> 4d14543 (6 stage is done)

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.university.mytaskcalendar.R
import ru.university.mytaskcalendar.Room.AppDatabase
import ru.university.mytaskcalendar.Task
import ru.university.mytaskcalendar.Repository.TaskRepository
import ru.university.mytaskcalendar.ViewModels.TaskViewModel
import ru.university.mytaskcalendar.ViewModels.TaskViewModelFactory
import java.util.Calendar
import java.util.Locale

class AddEditTaskActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(AppDatabase.getDatabase(this).taskDao()))
    }

    private lateinit var tilTitle: TextInputLayout
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etDate: TextInputEditText
    private lateinit var etTime: TextInputEditText
    private lateinit var switchDone: MaterialSwitch

    private var taskId: Long = -1L
    private var existingTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        tilTitle = findViewById(R.id.tilTitle)
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        switchDone = findViewById(R.id.switchDone)

<<<<<<< HEAD

        taskId = intent.getLongExtra("task_id", -1L)

        if (taskId != -1L) {

=======
        taskId = intent.getLongExtra("task_id", -1L)

        if (taskId != -1L) {
            // РЕЖИМ РЕДАКТИРОВАНИЯ
>>>>>>> 4d14543 (6 stage is done)
            title = getString(R.string.title_edit_task)
            viewModel.loadTask(taskId) { task ->
                existingTask = task
                if (task != null) {
                    etTitle.setText(task.title)
                    etDescription.setText(task.description)
                    etDate.setText(task.date)
                    etTime.setText(task.time)
                    switchDone.isChecked = task.isDone
                }
            }
        } else {
<<<<<<< HEAD
            title = getString(R.string.title_add_task)
            val now = Calendar.getInstance()
            etDate.setText(formatDate(now))
            etTime.setText(formatTime(now))
        }


=======
            // РЕЖИМ СОЗДАНИЯ
            title = getString(R.string.title_add_task)

            // Если дата передана из календаря — используем её, иначе сегодняшнюю
            val presetDate = intent.getStringExtra("preset_date")
            etDate.setText(presetDate ?: formatDate(Calendar.getInstance()))
            etTime.setText(formatTime(Calendar.getInstance()))
        }

        // Клик по полю даты — открываем DatePickerDialog
>>>>>>> 4d14543 (6 stage is done)
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val picked = Calendar.getInstance()
                    picked.set(year, month, day)
                    etDate.setText(formatDate(picked))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

<<<<<<< HEAD
=======
        // Клик по полю времени — открываем TimePickerDialog
>>>>>>> 4d14543 (6 stage is done)
        etTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    val picked = Calendar.getInstance()
                    picked.set(Calendar.HOUR_OF_DAY, hour)
                    picked.set(Calendar.MINUTE, minute)
                    etTime.setText(formatTime(picked))
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
<<<<<<< HEAD
                true
            ).show()
        }


=======
                true // 24-часовой формат
            ).show()
        }

        // Кнопка «Сохранить»
>>>>>>> 4d14543 (6 stage is done)
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        val title = etTitle.text?.toString()?.trim().orEmpty()
        val description = etDescription.text?.toString()?.trim().orEmpty()
        val date = etDate.text?.toString().orEmpty()
        val time = etTime.text?.toString().orEmpty()
        val isDone = switchDone.isChecked

        // 1. Валидация заголовка
        if (title.isEmpty()) {
            tilTitle.error = getString(R.string.error_title_required)
            return
        }
        tilTitle.error = null

<<<<<<< HEAD
        // Валидация даты и времени: задача не может быть в прошлом.
        //    Пропускаем проверку для режима редактирования уже выполненной задачи —
        //    её дата может быть в прошлом, и это нормально.
=======
        // 2. Валидация даты и времени: задача не может быть в прошлом.
        // Пропускаем проверку для режима редактирования уже выполненной задачи —
        // её дата может быть в прошлом, и это нормально.
>>>>>>> 4d14543 (6 stage is done)
        val skipDateTimeCheck = existingTask?.isDone == true && isDone

        if (!skipDateTimeCheck) {
            val error = validateDateTime(date, time)
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                return
            }
        }

<<<<<<< HEAD

=======
        // 3. Сохранение
>>>>>>> 4d14543 (6 stage is done)
        if (existingTask != null) {
            val updated = existingTask!!.copy(
                title = title,
                description = description,
                date = date,
                time = time,
                isDone = isDone
            )
            viewModel.updateTask(updated)
        } else {
            val newTask = Task(
                title = title,
                description = description,
                date = date,
                time = time,
                isDone = isDone
            )
            viewModel.addTask(newTask)
        }

        Toast.makeText(this, R.string.task_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    /**
     * Проверяет, что выбранные дата и время не в прошлом.
     * Возвращает текст ошибки или null, если всё в порядке.
     */
    private fun validateDateTime(date: String, time: String): String? {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        format.isLenient = false

        val selected = try {
            format.parse("$date $time")
        } catch (e: Exception) {
            return getString(R.string.error_invalid_datetime)
        } ?: return getString(R.string.error_invalid_datetime)

        val now = Calendar.getInstance().time

        val diffMillis = selected.time - now.time
        if (diffMillis < -60_000L) {
            return getString(R.string.error_datetime_in_past)
        }

        return null
    }

    private fun formatDate(cal: Calendar): String {
        return String.format(
            Locale.getDefault(),
            "%04d-%02d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun formatTime(cal: Calendar): String {
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE)
        )
    }
}