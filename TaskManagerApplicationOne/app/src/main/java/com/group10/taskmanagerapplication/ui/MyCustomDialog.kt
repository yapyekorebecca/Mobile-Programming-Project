package com.group10.taskmanagerapplication.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.group10.taskmanagerapplication.R

class MyCustomDialog : DialogFragment() {
    private var taskToEdit: Task? = null
    private lateinit var firestore: FirebaseFirestore
    private var taskUpdatedCallback: TaskUpdatedCallback? = null

    fun setTaskUpdatedCallback(callback: TaskUpdatedCallback) {
        taskUpdatedCallback = callback
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        firestore = FirebaseFirestore.getInstance() // Initialize Firestore instance

        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog, null)

        val taskNameEditText: EditText = view.findViewById(R.id.et_username)
        val taskDescriptionEditText: EditText = view.findViewById(R.id.et_password)

        // Retrieve task data from arguments and populate dialog fields
        taskToEdit = arguments?.getParcelable(ARG_TASK)
        taskNameEditText.setText(taskToEdit?.name)
        taskDescriptionEditText.setText(taskToEdit?.description)

        builder.setView(view)
            .setTitle("Edit Task")
            .setPositiveButton("Update") { _, _ ->
                // Handle save button click
                val updatedTaskName = taskNameEditText.text.toString()
                val updatedTaskDescription = taskDescriptionEditText.text.toString()

                // Update the task in Firestore
                taskToEdit?.let {
                    it.name = updatedTaskName
                    it.description = updatedTaskDescription
                    updateTaskInFirestore(it)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
    }

    private fun updateTaskInFirestore(task: Task) {
        // Query the collection with the task name
        firestore.collection("tasks")
            .whereEqualTo("name", task.name)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Handle the case where no document was found
                    return@addOnSuccessListener
                }

                // Get the first document ID (assuming there's only one document with the same name)
                val documentId = documents.documents[0].id

                // Update the document with the retrieved ID
                firestore.collection("tasks")
                    .document(documentId)
                    .set(task)
                    .addOnSuccessListener {
                        taskUpdatedCallback?.onTaskUpdated()
                        Log.d("Firestore", "Document with $documentId successfully updated and ${task.name}")
                        // Notify the activity about the update
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Firestore", "Document with $documentId failed updated and ${task.name}")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Document with failed updated and ${task.name}")
            }
    }

    companion object {
        private const val ARG_TASK = "arg_task"

        fun newInstance(task: Task): MyCustomDialog {
            val args = Bundle().apply {
                putParcelable(ARG_TASK, task)
            }
            val fragment = MyCustomDialog()
            fragment.arguments = args
            return fragment
        }
    }
}