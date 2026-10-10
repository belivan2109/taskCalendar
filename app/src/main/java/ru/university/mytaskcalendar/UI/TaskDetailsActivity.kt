package ru.university.mytaskcalendar.UI

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.university.mytaskcalendar.R

class TaskDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        val taskId = intent.getLongExtra("task_id", -1L)

        findViewById<TextView>(R.id.tvDetailsTitle).text =
            getString(R.string.title_details)
        findViewById<TextView>(R.id.tvDetailsId).text =
            getString(R.string.task_id_label, taskId)
    }
}