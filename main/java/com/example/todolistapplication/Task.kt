package com.example.todolistapplication

data class Task(
    var id: Int,
    var description: String,
    var isDone: Boolean = false
)
