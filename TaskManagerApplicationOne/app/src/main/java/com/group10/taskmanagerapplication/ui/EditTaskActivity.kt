package com.group10.taskmanagerapplication.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.group10.taskmanagerapplication.R
import com.group10.taskmanagerapplication.databinding.ActivityEditTaskBinding
import java.util.Calendar

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var firestore: FirebaseFirestore
    private var taskId: String? = null
    private lateinit var editStartDate: EditText
    private lateinit var editEndDate: EditText
    private lateinit var btnStartDate: ImageButton
    private lateinit var btnEndDate: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editStartDate = findViewById(R.id.editStartDate)
        editEndDate = findViewById(R.id.editEndDate)
        btnStartDate = findViewById(R.id.btnStartDate)
        btnEndDate = findViewById(R.id.btnEndDate)

        // Set click listeners for the date picker buttons
        btnStartDate.setOnClickListener { showDatePicker(editStartDate) }
        btnEndDate.setOnClickListener { showDatePicker(editEndDate) }
        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            //  saveTask()
        }


        /* // Initialize Firestore
          firestore = FirebaseFirestore.getInstance()

          // Extract task ID from intent extras
          taskId = intent.getStringExtra("taskId")

          if (taskId == null) {
              // Handle null taskId gracefully (e.g., show a toast and finish the activity)
              Toast.makeText(this, "Task ID is null", Toast.LENGTH_SHORT).show()
              finish()
              return
          }


          // Set click listener for save button
          binding.btnSave.setOnClickListener {
              // Update task details in Firestore
              updateTask()
          }

          // Fetch and display task details
          fetchTaskDetails()
      }

      private fun fetchTaskDetails() {
          taskId?.let { taskId ->
              // Retrieve task details from Firestore using the task ID
              firestore.collection("tasks").document(taskId)
                  .get()
                  .addOnSuccessListener { documentSnapshot ->
                      val taskName = documentSnapshot.getString("taskName")
                      val description = documentSnapshot.getString("description")
                      val startDate = documentSnapshot.getString("startDate")
                      val endDate = documentSnapshot.getString("endDate")

                      // Populate EditText fields with retrieved task details
                      binding.editTaskName.setText(taskName)
                      binding.editDescription.setText(description)
                      binding.editStartDate.setText(startDate)
                      binding.editEndDate.setText(endDate)
                  }
                  .addOnFailureListener { exception ->
                      // Handle failure
                  }
          }
      }

      private fun updateTask() {
          taskId?.let { taskId ->
              val taskName = binding.editTaskName.text.toString()
              val description = binding.editDescription.text.toString()
              val startDate = binding.editStartDate.text.toString()
              val endDate = binding.editEndDate.text.toString()

              // Update task details in Firestore
              val task = hashMapOf(
                  "taskName" to taskName,
                  "description" to description,
                  "startDate" to startDate,
                  "endDate" to endDate
              )

              firestore.collection("tasks").document(taskId)
                  .update(task as Map<String, Any>)
                  .addOnSuccessListener {
                      // Task updated successfully
                      finish()
                  }
                  .addOnFailureListener { exception ->
                      // Handle failure
                  }
          }*/}

    fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                editText.setText(selectedDate)
                showTimePicker(editText)
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()

    }
    fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = "$selectedHour:$selectedMinute"
                val currentText = editText.text.toString()
                editText.setText("$currentText $selectedTime")
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

}
