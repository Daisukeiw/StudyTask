package com.example.studytask.data.model

enum class TaskStatus {
    PENDING,
    DOING,
    DONE
}

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val subject: String = "",
    val dueDate: String = "",
    val status: TaskStatus = TaskStatus.PENDING,
    val priority: Int = 1,
    val userId: String = "",
    val createdAt: Long = 0L
)
