package com.example.todoapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todoapp.model.Task

@Dao
interface TaskDao {
//queries ashan nmsk mn el tasks
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllTasks(userId: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0 ORDER BY timestamp DESC")
    fun getActiveTasks(userId: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 1 ORDER BY timestamp DESC")
    fun getCompletedTasks(userId: String): LiveData<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun deleteAllTasks(userId: String)
}