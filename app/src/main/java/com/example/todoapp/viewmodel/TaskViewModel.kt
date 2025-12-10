package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.local.TaskDatabase
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val allTasks: LiveData<List<Task>>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.getAllTasks(getCurrentUserId())
    }

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun addTask(title: String, priority: Priority) {
        if (title.isBlank()) return

        val task = Task(
            title = title,
            priority = priority,
            userId = getCurrentUserId()
        )

        viewModelScope.launch {
            repository.insert(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        viewModelScope.launch {
            repository.update(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            repository.deleteAllTasks(getCurrentUserId())
        }
    }
}