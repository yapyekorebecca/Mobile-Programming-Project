package com.group10.taskmanagerapplication.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.group10.taskmanagerapplication.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private lateinit var buttonSubmit: Button
    private lateinit var imageViewStartDate: ImageView
    private lateinit var imageViewEndDate: ImageView
    private lateinit var textViewStartDate: TextView
    private var currentYear: Int? = null
    private lateinit var mTaskEdit: EditText
    //private lateinit var firestore: FirebaseFirestore
    private lateinit var textViewEndDate: TextView
    private var isUpdate = false
    private var taskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Initialize views
        buttonSubmit = findViewById(R.id.buttonSubmit)
        imageViewStartDate = findViewById(R.id.imageViewStartDate)
        imageViewEndDate = findViewById(R.id.imageViewEndDate)
        textViewStartDate = findViewById(R.id.textStartDate)
        textViewEndDate = findViewById(R.id.textEndDate)
        currentYear = Calendar.getInstance().get(Calendar.YEAR)
        mTaskEdit = findViewById(R.id.editTaskName)

        isUpdate = intent.getBooleanExtra("isUpdate", false)
        taskId = intent.getStringExtra("taskId")

        mTaskEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) {
                    buttonSubmit.isEnabled = false
                    buttonSubmit.setBackgroundColor(ContextCompat.getColor(this@AddTaskActivity, android.R.color.darker_gray))
                } else {
                    buttonSubmit.isEnabled = true
                    buttonSubmit.setBackgroundColor(ContextCompat.getColor(this@AddTaskActivity, R.color.purple))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        imageViewStartDate.setOnClickListener {
            showDatePickerDialogForStartDate()
        }

        imageViewEndDate.setOnClickListener {
            showDatePickerDialogForEndDate()
        }

        buttonSubmit.setOnClickListener {
            if (allFieldsFilled()) {
                submitTask()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allFieldsFilled(): Boolean {
        val taskName = mTaskEdit.text.toString()
        val description = findViewById<EditText>(R.id.editDescription)?.text.toString()
        val startDate = textViewStartDate.text.toString()
        val endDate = textViewEndDate.text.toString()

        return taskName.isNotBlank() && description.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()
    }

    private fun submitTask() {

        val taskName = mTaskEdit.text.toString()
        val description = findViewById<EditText>(R.id.editDescription)?.text.toString()
        val startDate = textViewStartDate.text.toString()
        val endDate = textViewEndDate.text.toString()

        val intent = Intent()
        intent.putExtra("taskName", taskName)
        setResult(Activity.RESULT_OK, intent)
        finish()

        if (taskName.isBlank()) {
            Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val task = hashMapOf(
            "taskName" to taskName,
            "description" to description,
            "startDate" to startDate,
            "endDate" to endDate,
            "completionStatus" to false,
            "createdAt" to Calendar.getInstance().time
        )

        val firestore = FirebaseFirestore.getInstance()

        if (isUpdate) {
            if (!taskId.isNullOrBlank()) {
                firestore.collection("tasks").document(taskId!!)
                    .update(task as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating task: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show()
            }
        } else {
            firestore.collection("tasks")
                .add(task)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding task: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }



    private fun showDatePickerDialogForStartDate() {
        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            // Invoke TimePickerDialog here
            showTimePickerDialog(true, selectedDate)

            // Update textViewStartDate
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val formattedDateTime = dateFormat.format(selectedDate.time)
            textViewStartDate.text = formattedDateTime
        }, currentYear!!, 0, 1)
        datePicker.show()
    }


    private fun showDatePickerDialogForEndDate() {
        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            // Invoke TimePickerDialog here
            showTimePickerDialog(false, selectedDate)
        }, currentYear!!, 0, 1)
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
                textViewStartDate.text = formattedDateTime
            } else {
                textViewEndDate.text = formattedDateTime
                // If end date is selected, do not submit automatically
            }
        }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), false)
        timePicker.show()
    }
}
