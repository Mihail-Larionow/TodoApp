package com.michel.core.date.models

data class TodoItem(
    val id: String,
    var text: String,
    var priority: Priority,
    var deadline: Long? = null,
    var isDone: Boolean,
    var dateChanged: Long? = null
)

sealed class Priority(val text: String) {
    data object High: Priority("!! Высокий")
    data object Standard: Priority("Нет")
    data object Low: Priority("Низкий")
}

fun emptyTodoItem(): TodoItem {
    return TodoItem(
        id = "",
        text = "",
        priority = Priority.Standard,
        isDone = false
    )
}
