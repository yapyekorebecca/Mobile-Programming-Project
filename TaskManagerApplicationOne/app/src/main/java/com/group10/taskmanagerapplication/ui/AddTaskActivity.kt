package com.group10.taskmanagerapplication.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.group10.taskmanagerapplication.reminders.ReminderReceiver
import com.group10.taskmanagerapplication.reminders.notificationID
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

        // Check for empty task name upfront
        if (taskName.isBlank()) {
            Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val startDateLong = dateFormat.parse(startDate)
        if (startDateLong == null) {
            Toast.makeText(this, "Failed to parse start date", Toast.LENGTH_LONG).show()
            return
        }

        // Convert date to milliseconds for showAlert
        val calendar = Calendar.getInstance()
        calendar.time = startDateLong
        showAlert(calendar.timeInMillis, taskName, description)

        // Continue with the Firestore operation
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
                firestore.collection("task").document(taskId!!)
                    .update(task as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                        scheduleNotification()
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
                    scheduleNotification()
                }
                .addOnFailureListener { e ->
                    print("${e.message}")
                    Toast.makeText(this, "Error adding task: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }



    private fun showDatePickerDialogForStartDate() : Calendar {
        var selectedDate : Calendar = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val datePicker = DatePickerDialog(this, { _, yearSelected, monthSelected, dayOfMonth ->
             selectedDate = Calendar.getInstance()
            selectedDate.set(yearSelected, monthSelected, dayOfMonth)
            // Invoke TimePickerDialog here
            showTimePickerDialog(true, selectedDate)
        }, year, month, day)
        datePicker.show()
        return selectedDate
    }

    private fun showDatePickerDialogForEndDate() {
        var selectedDate : Calendar = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val datePicker = DatePickerDialog(this, { _, yearSelected, monthSelected, dayOfMonth ->
            selectedDate = Calendar.getInstance()
            selectedDate.set(yearSelected, monthSelected, dayOfMonth)
            // Invoke TimePickerDialog here
            showTimePickerDialog(false, selectedDate)
        }, year, month, day)
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
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification() {
        // Create an Intent for the ReminderReceiver
        val intent = Intent(this@AddTaskActivity, ReminderReceiver::class.java)
        val taskName = mTaskEdit.text.toString()
        val description = findViewById<EditText>(R.id.editDescription)?.text.toString()
        val startDateString = textViewStartDate.text.toString()

        // Put extras that the ReminderReceiver will need
        intent.putExtra("titleExtra", taskName)
        intent.putExtra("messageExtra", description)

        // Create a PendingIntent to be triggered by the AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            this@AddTaskActivity,
            notificationID, // Ensure this is a unique ID for each notification
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Parse the start date and time from the string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val startDate: Date? = dateFormat.parse(startDateString)
        if (startDate == null) {
            Toast.makeText(this, "Failed to parse start date", Toast.LENGTH_LONG).show()
            return
        }

        // Set the time in a Calendar object
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        // Get the AlarmManager service
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the exact alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        // Show toast for user confirmation or debugging (optional)
        Toast.makeText(this, "$taskName \n $description", Toast.LENGTH_SHORT).show()
    }


    private fun showAlert(time: Long, title: String, message: String) {
        if (!isFinishing) {
            val date = Date(time)
            val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.getDefault())
            val timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.getDefault())

            AlertDialog.Builder(this@AddTaskActivity)
                .setTitle("Notification Scheduled")
                .setMessage("Title: $title\nMessage: $message\nAt: ${dateFormat.format(date)} ${timeFormat.format(date)}")
                .setPositiveButton("Okay") { _, _ ->
                    // Finish the activity when the user clicks "OK"
                    finish()
                }
                .show()
        }
    }


    }
