package ru.university.mytaskcalendar

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        val taskId = intent.getIntExtra("task_id", -1)

        findViewById<TextView>(R.id.tvDetailsTitle).text = getString(R.string.task_details_title)
        findViewById<TextView>(R.id.tvDetailsId).text = getString(R.string.task_id_label, taskId)
    }
}