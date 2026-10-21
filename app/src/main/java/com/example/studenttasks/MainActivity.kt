package com.example.studenttasks

import android.R
import android.icu.text.CaseMap
import android.os.Bundle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studenttasks.ui.theme.StudentTasksTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner


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
                        // AddTaskExample()
                        StudentTasksApp()
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskExample() {
    var taskTitle by rememberSaveable() {mutableStateOf("")}

    // 04-state-and-interaction: Challenge 3
    var addAttempts by rememberSaveable() {mutableStateOf(0)}


    Column() {
        OutlinedTextField(
            value = taskTitle, onValueChange = {newValue -> taskTitle = newValue},
            label = {Text("Task title")}
        )
        Text("You typed: $taskTitle")

        // 04-state-and-interaction: Challenge 3
        Text("Tasks added: $addAttempts")
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 04-state-and-interaction: Challenge 1
        Button(onClick = {taskTitle = ""}) {Text("Clear")}

        // 04-state-and-interaction: Challenge 2-3
        Button(onClick = {addAttempts++}, enabled = taskTitle.isNotBlank()) {
            Text("Add Task")
        }

    }

}

@Composable
fun StudentTasksApp(modifier: Modifier = Modifier) {
    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(1, "Prepare Kotlin exercise", isCompleted = true, priority = Priority.LOW),
                Task(2, "Read Android documentation", priority = Priority.HIGH),
                Task(3, "Run the app", priority = Priority.MEDIUM),
            )
        )
    }

    var newTaskTitle by rememberSaveable { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var priorityMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val completedCount = tasks.count { it.isCompleted }

        // 06-working-task-app: Challenge 1
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$completedCount of ${tasks.size} completed")

            Button(
                onClick = { tasks = tasks.filter { !it.isCompleted } },
                enabled = completedCount > 0
            ) {
                Text("Delete Completed")
            }
        }

        // 06-working-task-app: Challenge 4
        val progressPercentage = if (tasks.isEmpty()) {
            0
        } else {
            completedCount * 100 / tasks.size
        }

        Text("Progress: $progressPercentage%")

        OutlinedTextField(
            value = newTaskTitle,
            onValueChange = { newValue -> newTaskTitle = newValue },
            label = { Text("New Task") },
            modifier = Modifier.fillMaxWidth()
        )

        // 06-working-task-app: Challenge 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown Menu لاختيار الأولوية
            Box {
                OutlinedButton(onClick = { priorityMenuExpanded = true }) {
                    Text("Priority: ${selectedPriority.name}")
                }

                DropdownMenu(
                    expanded = priorityMenuExpanded,
                    onDismissRequest = { priorityMenuExpanded = false }
                ) {
                    Priority.entries.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority.name) },
                            onClick = {
                                selectedPriority = priority
                                priorityMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // زر الإضافة
            Button(
                onClick = {
                    val newTask = Task(
                        id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
                        title = newTaskTitle.trim(),
                        priority = selectedPriority
                    )
                    tasks = tasks + newTask
                    newTaskTitle = ""
                    selectedPriority = Priority.MEDIUM // إعادة تعيين للقيمة الافتراضية
                },
                enabled = newTaskTitle.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Add Task")
            }
        }

        Text(text = "Student Tasks", style = MaterialTheme.typography.headlineMedium)

        // 05-models-and-lists: Challenge 3
        val sortedTasks = tasks.sortedBy { task ->
            when (task.priority) {
                Priority.HIGH -> 1
                Priority.MEDIUM -> 2
                Priority.LOW -> 3
            }
        }
        Text("${sortedTasks.size} tasks")

        TaskList(
            tasks = tasks,
            onCompletedChange = { changedTask, isCompleted ->
                tasks = tasks.map { task ->
                    if (task.id == changedTask.id) {
                        task.copy(isCompleted = isCompleted)
                    } else {
                        task
                    }
                }
            },
            onDelete = { taskToDelet ->
                tasks = tasks.filter { task -> task.id != taskToDelet.id }
            }
        )
    }

    // 05-models-and-lists: Challenge 4
    val pendingTasks = tasks.filter { !it.isCompleted }
    println(pendingTasks)
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
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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