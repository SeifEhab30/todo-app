package com.example.todoapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.local.TaskDao
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TaskRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var taskDao: TaskDao

    private lateinit var repository: TaskRepository

    private val testUserId = "test_user_123"

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = TaskRepository(taskDao)
    }

    @Test
    fun `getAllTasks should return LiveData from DAO`() {
        // Given
        val testTasks = listOf(
            Task(1, "Task 1", Priority.HIGH, userId = testUserId),
            Task(2, "Task 2", Priority.LOW, userId = testUserId)
        )
        val liveData = MutableLiveData(testTasks)
        whenever(taskDao.getAllTasks(testUserId)).thenReturn(liveData)

        // When
        val result = repository.getAllTasks(testUserId)

        // Then
        verify(taskDao).getAllTasks(testUserId)
        assert(result.value == testTasks)
    }

    @Test
    fun `getActiveTasks should return only non-completed tasks`() {
        // Given
        val activeTasks = listOf(
            Task(1, "Active Task", Priority.MEDIUM, isCompleted = false, userId = testUserId)
        )
        val liveData = MutableLiveData(activeTasks)
        whenever(taskDao.getActiveTasks(testUserId)).thenReturn(liveData)

        // When
        val result = repository.getActiveTasks(testUserId)

        // Then
        verify(taskDao).getActiveTasks(testUserId)
        assert(result.value?.all { !it.isCompleted } == true)
    }

    @Test
    fun `getCompletedTasks should return only completed tasks`() {
        // Given
        val completedTasks = listOf(
            Task(1, "Completed Task", Priority.LOW, isCompleted = true, userId = testUserId)
        )
        val liveData = MutableLiveData(completedTasks)
        whenever(taskDao.getCompletedTasks(testUserId)).thenReturn(liveData)

        // When
        val result = repository.getCompletedTasks(testUserId)

        // Then
        verify(taskDao).getCompletedTasks(testUserId)
        assert(result.value?.all { it.isCompleted } == true)
    }

    @Test
    fun `insert should call DAO insert method`() = runTest {
        // Given
        val task = Task(
            title = "New Task",
            priority = Priority.HIGH,
            userId = testUserId
        )

        // When
        repository.insert(task)

        // Then
        verify(taskDao).insert(task)
    }

    @Test
    fun `update should call DAO update method`() = runTest {
        // Given
        val task = Task(
            id = 1,
            title = "Updated Task",
            priority = Priority.MEDIUM,
            isCompleted = true,
            userId = testUserId
        )

        // When
        repository.update(task)

        // Then
        verify(taskDao).update(task)
    }

    @Test
    fun `delete should call DAO delete method`() = runTest {
        // Given
        val task = Task(
            id = 1,
            title = "Task to Delete",
            priority = Priority.LOW,
            userId = testUserId
        )

        // When
        repository.delete(task)

        // Then
        verify(taskDao).delete(task)
    }

    @Test
    fun `deleteAllTasks should call DAO deleteAllTasks method`() = runTest {
        // When
        repository.deleteAllTasks(testUserId)

        // Then
        verify(taskDao).deleteAllTasks(testUserId)
    }

    @Test
    fun `repository should handle multiple operations correctly`() = runTest {
        // Given
        val task1 = Task(1, "Task 1", Priority.HIGH, userId = testUserId)
        val task2 = Task(2, "Task 2", Priority.LOW, userId = testUserId)

        // When
        repository.insert(task1)
        repository.insert(task2)
        repository.update(task1.copy(isCompleted = true))
        repository.delete(task2)

        // Then
        verify(taskDao, times(2)).insert(org.mockito.kotlin.any())
        verify(taskDao, times(1)).update(org.mockito.kotlin.any())
        verify(taskDao, times(1)).delete(org.mockito.kotlin.any())
    }
}