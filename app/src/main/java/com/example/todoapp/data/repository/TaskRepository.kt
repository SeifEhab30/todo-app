package com.example.todoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.todoapp.data.local.TaskDao
import com.example.todoapp.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(userId: String): LiveData<List<Task>> {
        return taskDao.getAllTasks(userId)
    }

    fun getActiveTasks(userId: String): LiveData<List<Task>> {
        return taskDao.getActiveTasks(userId)
    }

    fun getCompletedTasks(userId: String): LiveData<List<Task>> {
        return taskDao.getCompletedTasks(userId)
    }

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    suspend fun deleteAllTasks(userId: String) {
        taskDao.deleteAllTasks(userId)
    }
}