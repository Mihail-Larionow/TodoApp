package com.michel.core.data

import java.util.Date

data class TodoItem(
    val id: String,
    val text: String,
    val priority: Priority,
    val deadline: Date? = null,
    val isDone: Boolean,
    val dateChanged: Date? = null
)

class Priority {
    companion object {
        val HIGH: Priority = Priority()
        val STANDARD: Priority = Priority()
        val LOW: Priority = Priority()
    }
}
