package com.example.studenttasks

import android.icu.text.CaseMap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding),
                           verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        TaskRow(title = "Prepare Exercise", isCompleted = false,
                            priority = Priority.HIGH)

                        TaskRow(title = "Read Documentation", isCompleted = true,
                            priority = Priority.MEDIUM)

                        TaskRow(title = "Run Android App", isCompleted = true,
                            priority = Priority.LOW)
                    }
                }
            }
        }
    }
}

// 01-android-studio-first-app.md: Challenge 1 and 2
@Composable
fun StudentTasksApp(modifier : Modifier = Modifier) {
    Column {
        Text("My Student Tasks")
        Text("0 tasks")
    }
}

// 03-compose-ui-layouts.md: Challenge 1
@Composable
fun TaskRow(title: String, isCompleted: Boolean, priority: Priority) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isCompleted, onCheckedChange = null)
        Text(text = title, modifier = Modifier.padding(start = 8.dp),
             // 03-compose-ui-layouts.md: Challenge 3
             style = MaterialTheme.typography.headlineLarge)
        Text(priority.toString(), modifier = Modifier.padding(start = 12.dp))
    }
}