package com.michel.feature.screens.todoitemscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michel.core.data.TodoItemsRepository
import com.michel.core.data.models.Priority
import com.michel.core.data.models.TodoItem
import com.michel.core.data.models.emptyTodoItem
import com.michel.core.data.utils.ResourceState
import com.michel.feature.screens.todoitemscreen.utils.ItemScreenEffect
import com.michel.feature.screens.todoitemscreen.utils.ItemScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val todoItemId = savedStateHandle.get<String>("id") ?: ""
    private var todoItem: TodoItem = emptyTodoItem()

    private val _state = MutableStateFlow(
        ItemScreenState(
            text = todoItem.text,
            priority = todoItem.priority,
            hasDeadline = false,
            deadline = todoItem.deadline?: Date().time,
            datePickerExpanded = false,
            priorityMenuExpanded = false,
            loading = true,
            failed = false,
            errorMessage = ""
        )
    )

    val state: StateFlow<ItemScreenState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<ItemScreenEffect> = MutableSharedFlow()
    val effect: SharedFlow<ItemScreenEffect> = _effect.asSharedFlow()

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
            } catch (_: Exception) {
                _state.update {
                    it.copy(
                        failed = true,
                        errorMessage = "Неизвестная ошибка"
                    )
                }
            }
        }
    }

    private fun getInfo() {
        viewModelScope.launch {
            repository.getItem(todoItemId).collect{ res ->
                when(res) {
                    is ResourceState.Failed -> {
                        _state.update {
                            it.copy(
                                loading = false,
                                failed = true,
                                errorMessage = res.errorMessage ?: ""
                            )
                        }
                    }
                    is ResourceState.Success -> {
                        todoItem = res.data!!
                        _state.update {
                            it.copy(
                                loading = false,
                                failed = false,
                                text = todoItem.text,
                                priority = todoItem.priority,
                                hasDeadline = todoItem.deadline != null,
                                deadline = todoItem.deadline?: Date().time
                            )
                        }
                    }
                }
            }
        }
    }

    // Сохраняет таску в репозиторий
    private fun save() {
        _state.update {
            it.copy(
                loading = true
            )
        }

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

            repository.addOrUpdateItem(newTodoItem).collect { res ->
                when(res) {
                    is ResourceState.Failed -> {
                        _state.update {
                            it.copy(
                                loading = false
                            )
                        }
                        _effect.emit(
                            ItemScreenEffect.ShowSnackBarEffect(res.errorMessage!!)
                        )
                    }
                    is ResourceState.Success -> {
                        _effect.emit(ItemScreenEffect.LeaveScreenEffect)
                    }
                }
            }
        }
    }

    // Удаляет таску из репозитория
    private fun delete() {
        _state.update {
            it.copy(
                loading = true
            )
        }

        viewModelScope.launch {
            repository.deleteItem(todoItem.id).collect { res ->
                when(res) {
                    is ResourceState.Failed -> {
                        _state.update {
                            it.copy(
                                loading = false
                            )
                        }
                        _effect.emit(
                            ItemScreenEffect.ShowSnackBarEffect(res.errorMessage!!)
                        )
                    }
                    is ResourceState.Success -> {
                        _effect.emit(ItemScreenEffect.LeaveScreenEffect)
                    }
                }
            }
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
    val datePickerExpanded: Boolean,
    val loading: Boolean,
    val failed: Boolean,
    val errorMessage: String
)