package com.example.todoapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TaskAdapter(
    private val onTaskChecked: (Task) -> Unit,
    private val onTaskDeleted: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val taskCheckbox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        private val priorityBadge: TextView = itemView.findViewById(R.id.priorityBadge)
        private val priorityIndicator: View = itemView.findViewById(R.id.priorityIndicator)
        private val taskTimestamp: TextView = itemView.findViewById(R.id.taskTimestamp)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(task: Task) {
            taskTitle.text = task.title
            taskCheckbox.isChecked = task.isCompleted

            // Set priority badge and colors
            when (task.priority) {
                Priority.HIGH -> {
                    priorityBadge.text = "HIGH"
                    priorityBadge.setBackgroundColor(
                        ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
                    )
                    priorityIndicator.setBackgroundColor(
                        ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
                    )
                }
                Priority.MEDIUM -> {
                    priorityBadge.text = "MEDIUM"
                    priorityBadge.setBackgroundColor(
                        ContextCompat.getColor(itemView.context, android.R.color.holo_orange_dark)
                    )
                    priorityIndicator.setBackgroundColor(
                        ContextCompat.getColor(itemView.context, android.R.color.holo_orange_dark)
                    )
                }
                Priority.LOW -> {
                    priorityBadge.text = "LOW"
                    priorityBadge.setBackgroundColor(
                        ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark)
                    )
                    priorityIndicator.setBackgroundColor(
                        ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark)
                    )
                }
            }

            taskTimestamp.text = getTimeAgo(task.timestamp)

            if (task.isCompleted) {
                taskTitle.paintFlags = taskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                taskTitle.alpha = 0.5f
            } else {
                taskTitle.paintFlags = taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                taskTitle.alpha = 1.0f
            }

            taskCheckbox.setOnCheckedChangeListener { _, _ ->
                onTaskChecked(task)
            }

            deleteButton.setOnClickListener {
                onTaskDeleted(task)
            }
        }

        private fun getTimeAgo(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
                diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
                else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}