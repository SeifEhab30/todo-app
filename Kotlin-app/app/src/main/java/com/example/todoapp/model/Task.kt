package com.example.todoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority {
    LOW, MEDIUM, HIGH
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val priority: Priority,
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val userId: String
)