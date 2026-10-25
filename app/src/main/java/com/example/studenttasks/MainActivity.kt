package com.example.studenttasks

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studenttasks.ui.theme.StudentTasksTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // added inner padding to show the text under the notification bar
                    // 03-compose-ui-layouts.md: Challenge 2
                    /*
                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding),
                           verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        TaskRow(title = "Prepare Exercise", isCompleted = false,
                            priority = Priority.HIGH)

                        TaskRow(title = "Read Documentation", isCompleted = true,
                            priority = Priority.MEDIUM)

                        TaskRow(title = "Run Android App", isCompleted = true,
                            priority = Priority.LOW)
                    }
                     */
                    Column(Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {
                        StudentTasksApp()
                    }
                }
            }
        }
    }
}
@Composable
fun StudentTasksApp() {
    val navController = rememberNavController()

    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(1, "Prepare Kotlin exercise", true, Priority.LOW),
                Task(2, "Read Android documentation", priority = Priority.HIGH),
                Task(3, "Run the app", priority = Priority.MEDIUM)
            )
        )
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(
                tasks = tasks, onAddTask = { navController.navigate("add") },
                onCompletedChange = { changedTask, isCompleted ->
                    tasks = tasks.map { task ->
                        if (task.id == changedTask.id)
                            task.copy(isCompleted = isCompleted)
                        else
                            task
                    }
                },
                onDelete = { taskToDelete ->
                    // 07-navigation-and-debugging: Challenge 2
                    Log.d("StudentTasks", "Deleting task: ${taskToDelete.title}")
                    tasks = tasks.filter {
                        it.id != taskToDelete.id
                    }
                },
                onAboutClick = { navController.navigate("about") }
            )
        }

        composable("add") {
            AddTaskScreen(
                onSave = { title ->
                    val task = Task( id = (tasks.maxOfOrNull { it.id } ?: 0) + 1, title = title)
                    tasks = tasks + task
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("about") {
            AboutScreen()
        }
    }
}


@Composable
fun HomeScreen(
    tasks: List<Task>,
    onAddTask: () -> Unit,
    onCompletedChange: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    onAboutClick: () -> Unit
) {
    val completedCount = tasks.count { it.isCompleted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(text = "Student Tasks", style = MaterialTheme.typography.headlineLarge)

        Text(text = "$completedCount of ${tasks.size} completed")

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onAddTask) {
                Text("Add Task")
            }
            // About button
            FilledTonalButton(onClick = onAboutClick) {
                Text("About")
            }
        }

        if (tasks.isEmpty()) {
            Text("No tasks yet")
        } else {
            TaskList(
                tasks = tasks,
                onCompletedChange = onCompletedChange,
                onDelete = onDelete,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
fun AddTaskScreen(onSave: (String) -> Unit, onCancel: () -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Add Task",
            style = MaterialTheme.typography.headlineLarge
        )

        OutlinedTextField(
            value = title, onValueChange = { title = it }, label = { Text("Task title") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onSave(title.trim()) }, enabled = title.isNotBlank()) {
                Text("Save")
            }

            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun AboutScreen(){
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Student Tasks", style = MaterialTheme.typography.headlineLarge)
        Text("Introductory Kotlin/Android application")
    }
}

@Composable
fun TaskList(
    tasks: List<Task>, onCompletedChange: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit, modifier: Modifier = Modifier
) {
    /*
    // 05-models-and-lists: Challenge 1
    if (tasks.isEmpty()) Text("No tasks yet")
    */
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items=tasks, key = {task -> task.id}) {
            task -> TaskRow(task = task, onCompletedChange = {
                isCompleted -> onCompletedChange(task,isCompleted)
            }, onDelete = {onDelete(task)}
            )
        }
    }
}

@Composable
fun TaskRow(task: Task, onCompletedChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = task.isCompleted, onCheckedChange = onCompletedChange)
        Column(Modifier
            .padding(8.dp)
            .weight(1f)) {

            // 05-models-and-lists: Challenge 2
            Text(
                task.title, textDecoration = if (task.isCompleted) {
                    TextDecoration.LineThrough
                } else null
            )
            // 05-models-and-lists: Challenge 2
            Text(
                task.priority.name, style = MaterialTheme.typography.bodySmall,
                textDecoration = if (task.isCompleted) {TextDecoration.LineThrough} else null
            )
        }
        OutlinedButton(onClick = onDelete) { Text("Delete") }
    }
}
