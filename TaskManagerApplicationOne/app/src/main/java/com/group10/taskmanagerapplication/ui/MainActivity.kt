package com.group10.taskmanagerapplication.ui

//import SubTasksActivity
//import SubTasksActivity
//import SubTasksActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.group10.taskmanagerapplication.R

class MainActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Find the RecyclerView by its ID
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)

        // Find the add button by its ID
        val addButton: Button = findViewById(R.id.addButton)

        // Initialize TaskAdapter
        taskAdapter = TaskAdapter(mutableListOf()) { clickedTask ->
            // Handle task item click here
            // For example, start the SubTasksActivity with the task name
            val intent = Intent(this@MainActivity, SubTasksActivity::class.java)
            intent.putExtra("taskName", clickedTask.name)
            startActivity(intent)
        }

        // Set adapter to RecyclerView
        tasksRecyclerView.adapter = taskAdapter

        // Set onClick listener for the add button
        addButton.setOnClickListener {
            // Start the AddTaskActivity when the add button is clicked
            val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        // Fetch tasks from Firestore and display them
        fetchAndDisplayTasks()
    }

    private fun fetchAndDisplayTasks() {
        // Get reference to the "tasks" collection in Firestore
        val tasksRef = firestore.collection("tasks")

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
}