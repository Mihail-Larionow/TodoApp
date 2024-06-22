package com.michel.todoapp.todoitemscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.michel.core.date.TodoItemsRepository
import com.michel.core.date.models.Priority
import com.michel.core.date.models.TodoItem
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

    val screenState = ItemScreenState(
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
            dateChanged = Date().time
        )
        repository.addItem(newTodoItem)
    }

    // Удаляет из репозитория таску
    fun delete() {
        repository.deleteItem(todoItem)
    }

}

data class ItemScreenState(
    var text: String,
    var priority: Priority,
    var hasDeadline: Boolean,
    var deadline: Long
)