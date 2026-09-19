package com.example.studenttasks

data class Task(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false,
    // 02-kotlin-essentials.md
    // Challenge 1
    val priority: Priority = Priority.MEDIUM
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

// 02-kotlin-essentials.md: Challenge 2
fun describeTask(task: Task) : String {
    return task.title + "-" + task.priority
}

