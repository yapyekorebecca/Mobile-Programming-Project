package com.group10.taskmanagerapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
//import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group10.taskmanagerapplication.R
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale

class TaskAdapter(private val tasks: MutableList<Task>, private val clickListener: (Task) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTextView: TextView? = itemView.findViewById(R.id.name)
        private val checkBox: CheckBox? = itemView.findViewById(R.id.completeCheckBox)

        fun bind(task: Task) {
            taskTextView?.text = task.name
            checkBox?.isChecked = task.isCompleted

            // Set click listener for the entire item view
            itemView.setOnClickListener {
                clickListener(task) // Invoke the click listener with the clicked task
            }
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.task_item_cell, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return tasks.size
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
