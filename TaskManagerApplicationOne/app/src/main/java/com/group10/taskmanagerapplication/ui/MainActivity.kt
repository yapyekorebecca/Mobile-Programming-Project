package com.group10.taskmanagerapplication.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.group10.taskmanagerapplication.R
import com.group10.taskmanagerapplication.reminders.channelID

class MainActivity : AppCompatActivity(), TaskAdapter.EditTaskListener,TaskUpdatedCallback {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var df: DocumentReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        createNotificationChannel(this)

        firestore = FirebaseFirestore.getInstance()
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        val df = firestore.collection("tasks").document("1")

        val addButton: Button = findViewById(R.id.addButton)
        val tasksList = mutableListOf<Task>()
        val recyclerView = findViewById<RecyclerView>(R.id.tasksRecyclerView)

        taskAdapter = TaskAdapter(tasksList, this, firestore, recyclerView,this, this,df, this)
        tasksRecyclerView.adapter = taskAdapter

        val itemTouchHelper = ItemTouchHelper(SwipeToEditCallback(taskAdapter))
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)

        addButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddTaskActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAndDisplayTasks()
    }

    private fun fetchAndDisplayTasks() {
        // Get reference to the "tasks" collection in Firestore
        val tasksRef = firestore.collection("tasks").orderBy("createdAt", Query.Direction.DESCENDING)

        // Retrieve tasks from Firestore
        tasksRef.get()
            .addOnSuccessListener { documents ->
                val tasksList = mutableListOf<Task>()
                for (document in documents) {
                    val taskName = document.getString("taskName")
                    taskName?.let {
                        val task = Task(it)
                        tasksList.add(task)
                    }
                }
                // Update adapter with the new list of tasks
                taskAdapter.updateTasks(tasksList)
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "This channel is used for task reminders."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onEditTask(task: Task) {
        val dialog = MyCustomDialog()
        dialog.show(supportFragmentManager, "MyCustomFragment")
    }

    override fun onTaskUpdated() {
        fetchAndDisplayTasks()
    }
}
interface TaskUpdatedCallback {
    fun onTaskUpdated()
}