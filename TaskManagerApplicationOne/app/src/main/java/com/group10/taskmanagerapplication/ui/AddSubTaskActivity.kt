package com.group10.taskmanagerapplication.ui

import SubtasksAdapter
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
//import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.group10.taskmanagerapplication.R
import java.text.SimpleDateFormat
import java.util.*

class AddSubTaskActivity : AppCompatActivity() {
    private lateinit var buttonSave: Button
    private lateinit var editTextStartDate: TextView
    private lateinit var editTextEndDate: TextView
    private lateinit var imageViewStartDate: ImageView
    private lateinit var imageViewEndDate: ImageView
    private lateinit var subtaskEdit: EditText
    private var taskId: String? = null
    private var isUpdate = false
    private lateinit var subtasksAdapter: SubtasksAdapter // Assuming SubtasksAdapter is your RecyclerView adapter
    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sub_task)

        // Initialize views
        subtaskEdit = findViewById(R.id.editTextSubtaskName)
        editTextStartDate = findViewById(R.id.editTextStartDate)
        editTextEndDate = findViewById(R.id.editTextEndDate)
        imageViewStartDate = findViewById(R.id.imageViewStartDate)
        imageViewEndDate = findViewById(R.id.imageViewEndDate)
        buttonSave = findViewById(R.id.buttonAddSubtask)

        // Set OnClickListener for the ImageView to open DatePickerDialog
        imageViewStartDate.setOnClickListener {
            showDatePickerDialogForStartDate()
        }
        imageViewEndDate.setOnClickListener {
            showDatePickerDialogForEndDate()
        }
        buttonSave.setOnClickListener {
            if (allFieldsFilled()) {
                saveSubTask()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allFieldsFilled(): Boolean {
        val taskName = subtaskEdit.text.toString()
        val startDate = editTextStartDate.text.toString()
        val endDate = editTextEndDate.text.toString()

        return taskName.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()
    }

    private fun saveSubTask() {
        val subtaskName = subtaskEdit.text.toString()
        val startDate = editTextStartDate.text.toString()
        val endDate = editTextEndDate.text.toString()

        if (subtaskName.isBlank()) {
            Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val task = hashMapOf(
            "subtaskName" to subtaskName,
            "startDate" to startDate,
            "endDate" to endDate,
            "completionStatus" to false,
            "createdAt" to Calendar.getInstance().time
        )

        val firestore = FirebaseFirestore.getInstance()

        if (isUpdate) {
            if (!taskId.isNullOrBlank()) {
                firestore.collection("subTask").document(taskId!!)
                    .update(task as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Subtask updated successfully", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating subtask: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Invalid subtask ID", Toast.LENGTH_SHORT).show()
            }
        } else {
            firestore.collection("subTask")
                .add(task)
                .addOnSuccessListener { documentReference ->
                    // Pass subtask data back to calling activity
                    val resultIntent = Intent()
                    resultIntent.putExtra("subtaskName", subtaskName)
                    resultIntent.putExtra("startDate", startDate)
                    resultIntent.putExtra("endDate", endDate)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding subtask: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        val resultIntent = Intent()
        resultIntent.putExtra("subtaskName", subtaskName)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


    private fun showDatePickerDialogForStartDate() {
        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            // Invoke TimePickerDialog here
            showTimePickerDialog(true, selectedDate)
        }, currentYear, 0, 1)
        datePicker.show()
    }

    private fun showDatePickerDialogForEndDate() {
        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            // Invoke TimePickerDialog here
            showTimePickerDialog(false, selectedDate)
        }, currentYear, 0, 1)
        datePicker.show()
    }

    private fun showTimePickerDialog(isStartDate: Boolean, selectedDate: Calendar) {
        val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedDate.set(Calendar.MINUTE, minute)
            // Format the datetime
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val formattedDateTime = dateFormat.format(selectedDate.time)
            // Update the respective TextView based on whether it's start or end date
            if (isStartDate) {
                editTextStartDate.text = formattedDateTime
            } else {
                editTextEndDate.text = formattedDateTime
                // If end date is selected, do not submit automatically
            }
        }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), false)
        timePicker.show()
    }
}