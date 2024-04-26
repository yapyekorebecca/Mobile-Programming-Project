package com.group10.taskmanagerapplication.ui

import SubtasksAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.firestore.FirebaseFirestore
import com.group10.taskmanagerapplication.R
//import com.group10.taskmanagerapplication.ui.SubtasksAdapter

class SubTasksActivity : AppCompatActivity() {

    private lateinit var taskName: String
    private lateinit var subtasksRecyclerView: RecyclerView
    private lateinit var addSubtaskButton: Button
    private lateinit var attachmentIcon: ImageView
    private lateinit var adapter: SubtasksAdapter
    private val subtasksList = mutableListOf<String>()

    //private val FILE_PICKER_REQUEST_CODE = 1001

    private val sharedPrefs by lazy {
        getSharedPreferences("Subtasks", Context.MODE_PRIVATE)
    }

    private val startAddSubtaskActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val subtaskName = data?.getStringExtra("subtaskName")
                subtaskName?.let {
                    subtasksList.add(it)
                    adapter.notifyItemInserted(subtasksList.size - 1)
                    saveSubtaskToSharedPreferences(it) // Save subtask to SharedPreferences
                }
            }
        }

    private val filePickerActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//              //  val data: Intent? = result.data
//              //  val selectedFileUri = data?.data
//                // Process the selected file URI here, such as displaying it to the user or attaching it to the comment
//            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_tasks)

        // Retrieve task name passed from MainActivity
        taskName = intent.getStringExtra("taskName") ?: ""

        // Set up RecyclerView
        subtasksRecyclerView = findViewById(R.id.subtasksRecyclerView)
        subtasksRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with empty list
        adapter = SubtasksAdapter(subtasksList)
        subtasksRecyclerView.adapter = adapter

        // Find the "Add Subtask" button
        addSubtaskButton = findViewById(R.id.addSubtaskButton)
        // Find the attachment icon ImageView
        attachmentIcon = findViewById(R.id.attachment)

        // Set OnClickListener for the "Add Subtask" button
        addSubtaskButton.setOnClickListener {
            // Start the AddSubtaskActivity
            val intent = Intent(this@SubTasksActivity, AddSubTaskActivity::class.java)
            intent.putExtra("taskName", taskName)
            startAddSubtaskActivityForResult.launch(intent)
        }
        // Set OnClickListener for the attachment icon
        attachmentIcon.setOnClickListener {
            // Open file picker or attachment dialog here
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // Allow any type of file to be selected
            filePickerActivityResult.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch subtask data from SharedPreferences and update RecyclerView
        fetchSubtasksFromSharedPreferences()
    }

    private fun fetchSubtasksFromSharedPreferences() {
        // Retrieve subtasks from SharedPreferences based on the taskName
        val existingSubtasksSet = sharedPrefs.getStringSet(taskName, setOf())

        // Clear existing subtasks list
        subtasksList.clear()

        // Add fetched subtasks to the subtasksList
        existingSubtasksSet?.let {
            subtasksList.addAll(it)
        }

        // Notify the adapter of the data change
        adapter.notifyDataSetChanged()
    }

    private fun saveSubtaskToSharedPreferences(subtaskName: String) {
        // Retrieve existing subtasks from SharedPreferences
        val existingSubtasksSet = sharedPrefs.getStringSet(taskName, setOf())?.toMutableSet()

        // Add the new subtask to the copied set
        existingSubtasksSet?.add(subtaskName)

        // Save the updated subtasks back to SharedPreferences
        existingSubtasksSet?.let {
            sharedPrefs.edit {
                putStringSet(taskName, it)
            }
        }
    }
}
