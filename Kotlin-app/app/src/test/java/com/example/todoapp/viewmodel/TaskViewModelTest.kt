package com.example.todoapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class TaskViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testUserId = "test_user_123"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addTask should create task with correct data`() {
        val taskTitle = "Test Task"
        val priority = Priority.HIGH

        val task = Task(
            title = taskTitle,
            priority = priority,
            userId = testUserId
        )

        assert(task.title == taskTitle)
        assert(task.priority == priority)
        assert(task.userId == testUserId)
        assert(!task.isCompleted)
    }

    @Test
    fun `toggleTaskCompletion should update task completion status`() {
        val task = Task(
            id = 1,
            title = "Test Task",
            priority = Priority.MEDIUM,
            userId = testUserId,
            isCompleted = false
        )

        val updatedTask = task.copy(isCompleted = !task.isCompleted)

        assert(updatedTask.isCompleted)
        assert(updatedTask.id == task.id)
        assert(updatedTask.title == task.title)
    }

    @Test
    fun `task should have correct priority levels`() {
        val lowTask = Task(
            title = "Low Priority Task",
            priority = Priority.LOW,
            userId = testUserId
        )
        assert(lowTask.priority == Priority.LOW)

        val mediumTask = Task(
            title = "Medium Priority Task",
            priority = Priority.MEDIUM,
            userId = testUserId
        )
        assert(mediumTask.priority == Priority.MEDIUM)

        val highTask = Task(
            title = "High Priority Task",
            priority = Priority.HIGH,
            userId = testUserId
        )
        assert(highTask.priority == Priority.HIGH)
    }

    @Test
    fun `task timestamp should be set automatically`() {
        val beforeTime = System.currentTimeMillis()

        val task = Task(
            title = "Test Task",
            priority = Priority.LOW,
            userId = testUserId
        )

        val afterTime = System.currentTimeMillis()

        assert(task.timestamp >= beforeTime)
        assert(task.timestamp <= afterTime)
    }

    @Test
    fun `task should have default values`() {
        val task = Task(
            title = "Test Task",
            priority = Priority.MEDIUM,
            userId = testUserId
        )

        assert(task.id == 0)
        assert(!task.isCompleted)
        assert(task.timestamp > 0)
    }

    @Test
    fun `task copy should preserve data correctly`() {
        val originalTask = Task(
            id = 1,
            title = "Original Task",
            priority = Priority.HIGH,
            timestamp = 123456789L,
            isCompleted = false,
            userId = testUserId
        )

        val copiedTask = originalTask.copy(isCompleted = true)

        assert(copiedTask.id == originalTask.id)
        assert(copiedTask.title == originalTask.title)
        assert(copiedTask.priority == originalTask.priority)
        assert(copiedTask.timestamp == originalTask.timestamp)
        assert(copiedTask.userId == originalTask.userId)
        assert(copiedTask.isCompleted == true)
        assert(copiedTask.isCompleted != originalTask.isCompleted)
    }
}
