Todo List App
A simple and clean Android Todo List application built with modern Android development practices.
📱 Features

✅ Add new tasks
✅ Mark tasks as complete/incomplete
✅ Delete tasks
✅ Task difficulty levels (Easy, Medium, Hard)
✅ Persistent storage (tasks saved locally)
✅ Clean and responsive UI

🛠️ Technologies Used

Language: Kotlin
UI Framework: Jetpack Compose
Database: Room Database
Architecture: MVVM (Model-View-ViewModel)
Testing: JUnit, Mockito, Espresso

⚙️ Key Components
1. Room Database

Stores tasks persistently on device
Supports CRUD operations (Create, Read, Update, Delete)

2. MVVM Architecture

Model: Todo data class
View: Compose UI in MainActivity
ViewModel: TodoViewModel manages UI state and business logic

3. Repository Pattern

TodoRepository acts as a single source of truth
Separates data operations from UI logic


🚀 Getting Started

Clone the repository
Open in Android Studio
Sync Gradle
Run the app on an emulator or device

📝 License
This project is for educational purposes.