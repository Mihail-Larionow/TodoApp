package com.michel.core.data

import java.util.Date

data class TodoItem(
    val id: String,
    val text: String,
    val deadline: Date?,
    val isDone: Boolean,
    val dateChanged: Date?
)