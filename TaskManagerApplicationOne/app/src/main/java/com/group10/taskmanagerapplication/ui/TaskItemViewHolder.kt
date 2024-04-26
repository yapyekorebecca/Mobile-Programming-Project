package com.group10.taskmanagerapplication.ui

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group10.taskmanagerapplication.R


class TaskItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val checkboxView: CheckBox = itemView.findViewById(R.id.completeCheckBox)
    val taskNameTextView: TextView = itemView.findViewById(R.id.name)
}