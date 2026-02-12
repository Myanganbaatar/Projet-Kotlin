package com.example.projetkotlin

data class Task(
    val id: Int,
    val title: String,
    val description: String = "",
    val status: TaskStatus = TaskStatus.TODO
)

enum class TaskStatus {
    TODO, LATE, DONE
}
