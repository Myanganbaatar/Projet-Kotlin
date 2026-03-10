package com.example.projetkotlin

data class Task(
    val id: Int,
    val title: String,
    val description: String = "",
    val status: TaskStatus = TaskStatus.TODO,
    val periodicity: Periodicity = Periodicity.NONE
)

enum class TaskStatus {
    TODO, LATE, DONE
}

enum class Periodicity {
    NONE, DAILY, WEEKLY, MONTHLY
}
