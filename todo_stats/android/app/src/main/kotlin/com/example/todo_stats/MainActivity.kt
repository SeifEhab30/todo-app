// In the Flutter Stats App: android/app/src/main/kotlin/com/example/todo_stats/MainActivity.kt

package com.example.todo_stats

import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
// Removed: import android.util.Log

class MainActivity: FlutterActivity() {

    private val CHANNEL = "com.example.todoapp/stats"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->

            if (call.method == "getInitialStats") {

                // Read the Intent extras passed by the launching Todo App
                val totalTasks = intent.getIntExtra("TOTAL_TASKS", 0)
                val completedTasks = intent.getIntExtra("COMPLETED_TASKS", 0)
                val pendingTasks = intent.getIntExtra("PENDING_TASKS", 0)

                if (totalTasks == 0 && completedTasks == 0 && pendingTasks == 0) {
                    // Send null back to Dart if no data was received
                    result.success(null)
                } else {
                    // Send the data map back to Dart
                    val map = mapOf(
                        "total" to totalTasks,
                        "completed" to completedTasks,
                        "pending" to pendingTasks
                    )
                    result.success(map)
                }
            } else {
                result.notImplemented()
            }
        }
    }
}