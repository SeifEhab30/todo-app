import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const TodoStatsApp());
}

class TodoStatsApp extends StatelessWidget {
  const TodoStatsApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Todo Statistics',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const StatsScreen(),
    );
  }
}

class StatsScreen extends StatefulWidget {
  const StatsScreen({super.key});

  @override
  State<StatsScreen> createState() => _StatsScreenState();
}

class _StatsScreenState extends State<StatsScreen> {
  static const platform = MethodChannel('com.example.todoapp/stats');

  int totalTasks = 0;
  int completedTasks = 0;
  int pendingTasks = 0;
  bool isLoading = true;
  bool usingRealData = false;

  @override
  void initState() {
    super.initState();
    _loadStatistics();
  }

  Future<void> _loadStatistics() async {
    setState(() {
      isLoading = true;
      usingRealData = false;
    });

    try {
      final result = await platform.invokeMethod('getInitialStats');

      if (result != null) {
        setState(() {
          totalTasks = result['total'] ?? 0;
          completedTasks = result['completed'] ?? 0;
          pendingTasks = result['pending'] ?? 0;
          usingRealData = true;
          isLoading = false;
        });
      } else {
        throw Exception('Native code returned null. Using zero default.');
      }
    } catch (e) {
      setState(() {
        totalTasks = 0;
        completedTasks = 0;
        pendingTasks = 0;
        usingRealData = false;
        isLoading = false;
      });
    }
  }

  double get completionPercentage {
    if (totalTasks == 0) return 0;
    return (completedTasks / totalTasks) * 100;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: const Text('Task Statistics'),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadStatistics,
            tooltip: 'Refresh',
          ),
        ],
      ),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : Center(
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 16,
                    vertical: 8,
                  ),
                  decoration: BoxDecoration(
                    color: usingRealData
                        ? Colors.green.withOpacity(0.1)
                        : Colors.orange.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(20),
                    border: Border.all(
                      color: usingRealData
                          ? Colors.green.withOpacity(0.3)
                          : Colors.orange.withOpacity(0.3),
                    ),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(
                        usingRealData ? Icons.check_circle : Icons.info,
                        size: 16,
                        color:
                        usingRealData ? Colors.green : Colors.orange,
                      ),
                      const SizedBox(width: 8),
                      Text(
                        usingRealData ? 'Live Data' : 'Default Data',
                        style: TextStyle(
                          fontSize: 12,
                          color: usingRealData
                              ? Colors.green
                              : Colors.orange,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 20),
                const Text(
                  '📊 Your Todo Statistics',
                  style: TextStyle(
                    fontSize: 28,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 40),
                _buildStatCard(
                  icon: Icons.list,
                  title: 'Total Tasks',
                  value: totalTasks.toString(),
                  color: Colors.blue,
                ),
                const SizedBox(height: 16),
                _buildStatCard(
                  icon: Icons.check_circle,
                  title: 'Completed',
                  value: completedTasks.toString(),
                  color: Colors.green,
                ),
                const SizedBox(height: 16),
                _buildStatCard(
                  icon: Icons.pending,
                  title: 'Pending',
                  value: pendingTasks.toString(),
                  color: Colors.orange,
                ),
                const SizedBox(height: 32),
                Column(
                  children: [
                    Text(
                      'Completion Rate',
                      style: TextStyle(
                        fontSize: 18,
                        color: Colors.grey[700],
                      ),
                    ),
                    const SizedBox(height: 12),
                    SizedBox(
                      width: 200,
                      height: 200,
                      child: Stack(
                        alignment: Alignment.center,
                        children: [
                          SizedBox(
                            width: 200,
                            height: 200,
                            child: CircularProgressIndicator(
                              value: completionPercentage / 100,
                              strokeWidth: 12,
                              backgroundColor: Colors.grey[200],
                              valueColor: AlwaysStoppedAnimation<Color>(
                                _getProgressColor(completionPercentage),
                              ),
                            ),
                          ),
                          Text(
                            '${completionPercentage.toStringAsFixed(0)}%',
                            style: const TextStyle(
                              fontSize: 48,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                if (totalTasks == 0)
                  Container(
                    padding: const EdgeInsets.all(16),
                    margin: const EdgeInsets.only(top: 20),
                    decoration: BoxDecoration(
                      color: Colors.orange.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(
                        color: Colors.orange.withOpacity(0.3),
                      ),
                    ),
                    child: Row(
                      children: [
                        Icon(Icons.info_outline, color: Colors.orange),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            'No tasks yet! Start adding tasks in the Todo app.',
                            style: TextStyle(color: Colors.grey[700]),
                          ),
                        ),
                      ],
                    ),
                  ),
                const SizedBox(height: 40),
                ElevatedButton.icon(
                  onPressed: () {
                    SystemNavigator.pop();
                  },
                  icon: const Icon(Icons.arrow_back),
                  label: const Text('Back to Todo App'),
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 32,
                      vertical: 16,
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                if (!usingRealData)
                  Text(
                    'Launch this app from the Todo app\'s menu to see real data',
                    style: TextStyle(
                      fontSize: 12,
                      color: Colors.grey[600],
                      fontStyle: FontStyle.italic,
                    ),
                    textAlign: TextAlign.center,
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildStatCard({
    required IconData icon,
    required String title,
    required String value,
    required Color color,
  }) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withOpacity(0.3), width: 2),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: color.withOpacity(0.2),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(icon, size: 32, color: color),
          ),
          const SizedBox(width: 20),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: TextStyle(
                  fontSize: 16,
                  color: Colors.grey[700],
                ),
              ),
              const SizedBox(height: 4),
              Text(
                value,
                style: TextStyle(
                  fontSize: 32,
                  fontWeight: FontWeight.bold,
                  color: color,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Color _getProgressColor(double percentage) {
    if (percentage >= 75) return Colors.green;
    if (percentage >= 50) return Colors.orange;
    return Colors.red;
  }
}
