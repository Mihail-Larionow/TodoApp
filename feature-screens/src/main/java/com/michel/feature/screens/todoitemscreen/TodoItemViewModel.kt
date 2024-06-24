package com.michel.feature.screens.todoitemscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.michel.core.data.TodoItemsRepository
import com.michel.core.data.models.Priority
import com.michel.core.data.models.TodoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TodoItemViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val todoItemId = savedStateHandle.get<String>("id") ?: "none"
    private val todoItem: TodoItem = repository.getItem(id = todoItemId)

    private val screenState = ItemScreenState(
        text = todoItem.text,
        priority = todoItem.priority,
        hasDeadline = todoItem.deadline != null,
        deadline = todoItem.deadline ?: Date().time,
    )

    // Сохраняет в репозиторий таску
    fun save() {
        val newTodoItem = TodoItem(
            id = todoItemId,
            text = screenState.text,
            priority = screenState.priority,
            deadline = if(screenState.hasDeadline) screenState.deadline else null,
            isDone = todoItem.isDone,
            createdAt = todoItem.createdAt,
            changedAt = Date().time
        )
        repository.addItem(newTodoItem)
    }

    // Удаляет из репозитория таску
    fun delete() {
        repository.deleteItem(todoItem)
    }

    fun getText(): String {
        return screenState.text
    }

    fun setText(text: String) {
        screenState.text = text
    }

    fun getPriority(): Priority {
        return screenState.priority
    }

    fun setPriority(priority: Priority) {
        screenState.priority = priority
    }

    fun hasDeadline(): Boolean {
        return screenState.hasDeadline
    }

    fun setHasDeadline(state: Boolean) {
        screenState.hasDeadline = state
    }

    fun getDeadline(): Long {
        return screenState.deadline
    }

    fun setDeadline(date: Long) {
        screenState.deadline = date
    }
}

data class ItemScreenState(
    var text: String,
    var priority: Priority,
    var hasDeadline: Boolean,
    var deadline: Long
)