package com.michel.feature.screens.todoitemscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michel.core.data.TodoItemsRepository
import com.michel.core.data.models.Priority
import com.michel.core.data.models.TodoItem
import com.michel.core.data.models.emptyTodoItem
import com.michel.feature.screens.todoitemscreen.utils.ItemScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TodoItemViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val todoItemId = savedStateHandle.get<String>("id") ?: "none"
    private var todoItem: TodoItem = emptyTodoItem()

    private val _state = MutableStateFlow(
        ItemScreenState(
            text = todoItem.text,
            priority = todoItem.priority,
            hasDeadline = false,
            deadline = todoItem.deadline?: Date().time,
            datePickerExpanded = false,
            priorityMenuExpanded = false
        )
    )

    val state: StateFlow<ItemScreenState> = _state.asStateFlow()

    init { onEvent(ItemScreenEvent.GetItemInfoEvent) }

    internal fun onEvent(event: ItemScreenEvent) {
        viewModelScope.launch {
            try{
                when(event) {
                    ItemScreenEvent.GetItemInfoEvent -> getInfo()
                    is ItemScreenEvent.DeleteEvent -> delete()
                    is ItemScreenEvent.SaveEvent -> save()
                    is ItemScreenEvent.SetDeadlineDateEvent -> updateDeadline(event.deadline)
                    is ItemScreenEvent.SetDeadlineState -> updateHasDeadline(event.hasDeadline)
                    is ItemScreenEvent.SetPriorityEvent -> updatePriority(event.priority)
                    is ItemScreenEvent.SetTextEvent -> updateText(event.text)
                    is ItemScreenEvent.SetDatePickerState -> updateDatePickerState(event.isExpanded)
                    is ItemScreenEvent.SetPriorityMenuState -> updatePriorityMenuState(event.isExpanded)
                    ItemScreenEvent.ToListScreenEvent -> { }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun getInfo() {
        viewModelScope.launch {
            todoItem = repository.getItem(todoItemId)

            _state.update {
                it.copy(
                    text = todoItem.text,
                    priority = todoItem.priority,
                    hasDeadline = todoItem.deadline != null,
                    deadline = todoItem.deadline?: Date().time
                )
            }
        }
    }

    // Сохраняет таску в репозиторий
    private fun save() {
        viewModelScope.launch {
            val deadline = if(state.value.hasDeadline){
                state.value.deadline
            } else null

            val newTodoItem = TodoItem(
                id = todoItemId,
                text = state.value.text,
                priority = state.value.priority,
                deadline = deadline,
                isDone = todoItem.isDone,
                createdAt = todoItem.createdAt,
                changedAt = Date().time
            )
            repository.addOrUpdateItem(newTodoItem)
        }
    }

    // Удаляет таску из репозитория
    private fun delete() {
        viewModelScope.launch {
            repository.deleteItem(todoItem)
        }
    }

    private fun updateText(text: String) {
        _state.update {
            it.copy(text = text)
        }
    }

    private fun updatePriority(priority: Priority) {
        _state.update {
            it.copy(priority = priority)
        }
    }

    private fun updateHasDeadline(hasDeadline: Boolean) {
        _state.update {
            it.copy(hasDeadline = hasDeadline)
        }
    }

    private fun updateDeadline(deadline: Long) {
        _state.update {
            it.copy(deadline = deadline)
        }
    }

    private fun updatePriorityMenuState(isExpanded: Boolean) {
        _state.update {
            it.copy(priorityMenuExpanded = isExpanded)
        }
    }

    private fun updateDatePickerState(isExpanded: Boolean) {
        _state.update {
            it.copy(datePickerExpanded = isExpanded)
        }
    }
}

data class ItemScreenState(
    val text: String,
    val priority: Priority,
    val hasDeadline: Boolean,
    val deadline: Long,
    val priorityMenuExpanded: Boolean,
    val datePickerExpanded: Boolean
)