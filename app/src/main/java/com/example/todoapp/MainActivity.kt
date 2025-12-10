package com.example.todoapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.adapter.TaskAdapter
import com.example.todoapp.model.Priority
import com.example.todoapp.ui.auth.LoginActivity
import com.example.todoapp.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: View
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var toolbar: MaterialToolbar
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if user is logged in
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Initialize views
        recyclerView = findViewById(R.id.tasksRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        fabAddTask = findViewById(R.id.fabAddTask)
        toolbar = findViewById(R.id.toolbar)

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Setup RecyclerView
        setupRecyclerView()

        // Observe tasks
        observeTasks()

        // FAB click listener
        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskChecked = { task ->
                taskViewModel.toggleTaskCompletion(task)
            },
            onTaskDeleted = { task ->
                showDeleteConfirmationDialog(task)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }

    private fun observeTasks() {
        taskViewModel.allTasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)

            // Show/hide empty state
            if (tasks.isEmpty()) {
                emptyStateLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyStateLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun showAddTaskDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_task)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val taskNameInput = dialog.findViewById<TextInputEditText>(R.id.taskNameInput)
        val radioLow = dialog.findViewById<RadioButton>(R.id.radioLow)
        val radioMedium = dialog.findViewById<RadioButton>(R.id.radioMedium)
        val radioHigh = dialog.findViewById<RadioButton>(R.id.radioHigh)
        val addButton = dialog.findViewById<Button>(R.id.addButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

        addButton.setOnClickListener {
            val taskName = taskNameInput.text.toString().trim()

            if (taskName.isEmpty()) {
                taskNameInput.error = "Task name cannot be empty"
                return@setOnClickListener
            }

            val priority = when {
                radioLow.isChecked -> Priority.LOW
                radioMedium.isChecked -> Priority.MEDIUM
                radioHigh.isChecked -> Priority.HIGH
                else -> Priority.MEDIUM
            }

            taskViewModel.addTask(taskName, priority)
            Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(task: com.example.todoapp.model.Task) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete \"${task.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                taskViewModel.deleteTask(task)
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmationDialog()
                true
            }
            R.id.action_delete_all -> {
                showDeleteAllConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteAllConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete All Tasks")
            .setMessage("Are you sure you want to delete all tasks? This cannot be undone.")
            .setPositiveButton("Delete All") { _, _ ->
                taskViewModel.deleteAllTasks()
                Toast.makeText(this, "All tasks deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}