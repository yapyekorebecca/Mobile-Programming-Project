package com.group10.taskmanagerapplication.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.group10.taskmanagerapplication.R

class TaskAdapter(
    val tasks: MutableList<Task>,
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val recyclerView: RecyclerView,
    private val taskUpdatedCallback: TaskUpdatedCallback,
    val activity: MainActivity  ,// Context passed directly as an Activity for dialog creation
    private val df: DocumentReference,
    private val editTaskListener: EditTaskListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var task: Task? = null

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.name)
        val checkBoxView: CheckBox = itemView.findViewById(R.id.completeCheckBox)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        init {
            deleteButton.setOnClickListener {
                showDeleteConfirmation(adapterPosition)
            }
            editButton.setOnClickListener {
                val dialog = MyCustomDialog.newInstance(tasks[adapterPosition])
                dialog.show(activity.supportFragmentManager, "MyCustomDialog")
            }
            checkBoxView.setOnCheckedChangeListener { _, isChecked ->
                val task = tasks[adapterPosition]
                task.isCompleted = isChecked

            }
            taskNameTextView.setOnClickListener {
                val context = itemView.context
                context.startActivity(Intent(context, SubTasksActivity::class.java))
            }
        }

        fun bind(task: Task) {
            taskNameTextView.text = task.name
            checkBoxView.isChecked = task.isCompleted
            applyTextDecorations(task)
        }

        private fun applyTextDecorations(task: Task) {
            if (task.isCompleted) {
                val spannable = SpannableString(task.name).apply {
                    setSpan(StrikethroughSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                taskNameTextView.text = spannable
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_item_cell, parent, false)
        return TaskViewHolder(itemView)
    }
    private fun toggleTaskCompletion(position: Int) {
        task = tasks[position]
        task!!.isCompleted = !task!!.isCompleted
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
        holder.checkBoxView.setOnCheckedChangeListener { _, isChecked ->
            if (task.isCompleted != isChecked) {
                toggleTaskCompletion(position)
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun showDeleteConfirmation(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Task")
        builder.setMessage("Are you sure you want to delete this task?")
        builder.setPositiveButton("Yes") { _, _ ->
            val removedTask = tasks.removeAt(position)

            // Notify the adapter about the removal
            notifyItemRemoved(position)

            // Show an undo option to restore the deleted task
            Snackbar.make(recyclerView, "Task '${removedTask.name}' deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    tasks.add(position, removedTask)

                    // Notify the adapter about the insertion
                    notifyItemInserted(position)
                }
                .show()
            taskUpdatedCallback.onTaskUpdated()
            // Query the collection with the task name
            firestore.collection("tasks")
                .whereEqualTo("taskName", removedTask.name)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Handle the case where no document was found
                        return@addOnSuccessListener
                    }

                    // Get the first document ID (assuming there's only one document with the same title)
                    val documentId = documents.documents[0].id

                    // Delete the document with the retrieved ID
                    firestore.collection("tasks")
                        .document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Document with $documentId successfully deleted")
                        }
                        .addOnFailureListener { exception ->
                            // Handle any errors that occurred while deleting the task
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occurred while querying the collection
                }
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }

    interface EditTaskListener {
        fun onEditTask(task: Task)
    }

// Inside TaskViewHolder's init block


    fun deleteTask(taskId: String) {
        if (taskId.isBlank()) {
//            Toast.makeText(Context, "Error: Task ID is missing.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            firestore.collection("tasks").document(taskId).delete().addOnSuccessListener {
//                Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
//                Toast.makeText(context, "Failed to delete task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IllegalArgumentException) {
//            Toast.makeText(context, "Error deleting task: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    fun getContext(): Context{
        return activity
    }
    fun editTask(position: Int) {
        val task = tasks[position]
        val intent = Intent(activity, AddTaskActivity::class.java).apply {
            putExtra("task_id", task.taskId)
            putExtra("task_name", task.name)
            putExtra("task_description", task.description)
        }
        activity.startActivity(intent)
    }
    fun updateTasks(newTasks: List<Task>) {
        val oldTasks = ArrayList(tasks)
        val newTasksSet = HashSet(newTasks)

        // Find tasks to be removed and inserted
        val removedTasks = oldTasks.filterNot { newTasksSet.contains(it) }
        val insertedTasks = newTasks.filterNot { oldTasks.contains(it) }

        tasks.clear()
        tasks.addAll(newTasks)

        // Notify RecyclerView of specific changes
        removedTasks.forEach { removedTask ->
            val index = oldTasks.indexOf(removedTask)
            if (index != RecyclerView.NO_POSITION) {
                notifyItemRemoved(index)
            }
        }

        insertedTasks.forEach { insertedTask ->
            val index = newTasks.indexOf(insertedTask)
            if (index != RecyclerView.NO_POSITION) {
                notifyItemInserted(index)
            }
        }
    }

}