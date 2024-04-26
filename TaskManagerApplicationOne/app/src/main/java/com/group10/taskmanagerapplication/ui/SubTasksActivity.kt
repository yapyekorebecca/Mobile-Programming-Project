package com.group10.taskmanagerapplication.ui

import SubtasksAdapter
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.FirebaseFirestore
import com.group10.taskmanagerapplication.R
//import com.group10.taskmanagerapplication.ui.SubtasksAdapter

class SubTasksActivity : AppCompatActivity() {

    private lateinit var taskName: String
    private lateinit var subtasksRecyclerView: RecyclerView
    private lateinit var addSubtaskButton: Button
    private lateinit var attachmentIcon: ImageView
    private lateinit var adapter: SubtasksAdapter
    private lateinit var attachedFileTextView: TextView
    private val subtasksList = mutableListOf<String>()
    private val FILE_URI_KEY = "file_uris_key"
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 100

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
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    addSelectedFileUri(uri)
                }
            }
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
        attachmentIcon = findViewById(R.id.resourceAttachmentIcon)

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
    private fun addSelectedFileUri(uri: Uri) {
        val existingUris = sharedPrefs.getStringSet(FILE_URI_KEY, mutableSetOf()) ?: mutableSetOf()
        existingUris.add(uri.toString())
        sharedPrefs.edit {
            putStringSet(FILE_URI_KEY, existingUris)
        }
        updateAttachedFilesUI(existingUris)

        // Upload file URI to Firestore
        uploadFileToDatabase(uri)
    }
    private fun updateAttachedFilesUI(uris: Set<String>) {
        attachedFileTextView.text = ""
        uris.forEach { uriString ->
            val uri = Uri.parse(uriString)
            val fileName = getFileNameFromUri(uri)
            attachedFileTextView.append("$fileName\n")
        }
        attachedFileTextView.visibility = if (uris.isNotEmpty()) View.VISIBLE else View.GONE
    }
    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
            )
        }
    }
    private fun getFileNameFromUri(uri: Uri): String {
        var displayName = ""
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    displayName = cursor.getString(index)
                }
            }
        }
        return displayName
    }
    private fun uploadFileToDatabase(uri: Uri) {
        val db = FirebaseFirestore.getInstance()
        val document = db.collection("taskFile").document() // Assuming "taskFiles" is the collection name
        val fileName = getFileNameFromUri(uri)
        val fileData = hashMapOf(
            "fileName" to fileName,
            "filePath" to uri.toString(),
            "taskId" to taskName // Assuming taskName is the ID of the task associated with this file
        )

        document.set(fileData)
            .addOnSuccessListener {
                // File information saved successfully to Firestore
            }
            .addOnFailureListener { e ->
                // Handle any errors
            }
    }
}
