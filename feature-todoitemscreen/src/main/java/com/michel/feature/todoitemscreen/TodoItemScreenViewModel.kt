package com.michel.feature.todoitemscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import com.michel.core.data.models.emptyTodoItem
import com.michel.core.data.repository.IRepository
import com.michel.feature.todoitemscreen.utils.ItemScreenIntent
import com.michel.feature.todoitemscreen.utils.ItemScreenSideEffect
import com.michel.feature.todoitemscreen.utils.TodoItemScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TodoItemViewModel @Inject constructor(
    private val repository: IRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val todoItemId = savedStateHandle.get<String>("id") ?: ""
    private var todoItem: TodoItem = emptyTodoItem()

    private val _state = MutableStateFlow(
        TodoItemScreenState(
            text = todoItem.text,
            importance = todoItem.importance,
            hasDeadline = false,
            deadline = todoItem.deadline ?: Date().time,
            datePickerExpanded = false,
            priorityMenuExpanded = false,
            loading = true,
            failed = false,
            errorMessage = ""
        )
    )

    val state: StateFlow<TodoItemScreenState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<ItemScreenSideEffect> = MutableSharedFlow()
    val effect: SharedFlow<ItemScreenSideEffect> = _effect.asSharedFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    init {
        onEvent(ItemScreenIntent.GetItemInfoIntent)
    }

    internal fun onEvent(event: ItemScreenIntent) {
        try {
            when (event) {
                ItemScreenIntent.GetItemInfoIntent -> getInfo()
                is ItemScreenIntent.DeleteIntent -> delete()
                is ItemScreenIntent.SaveIntent -> save()
                is ItemScreenIntent.SetDeadlineDateIntent -> updateDeadline(event.deadline)
                is ItemScreenIntent.SetDeadlineStateIntent -> updateHasDeadline(event.hasDeadline)
                is ItemScreenIntent.SetPriorityIntent -> updatePriority(event.importance)
                is ItemScreenIntent.SetTextIntent -> updateText(event.text)
                is ItemScreenIntent.SetDatePickerStateIntent -> updateDatePickerState(event.isExpanded)
                is ItemScreenIntent.SetPriorityMenuStateIntent -> updatePriorityMenuState(event.isExpanded)
                ItemScreenIntent.ToListScreenIntent -> {}
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

    private fun getInfo() {
        _state.update {
            it.copy(
                loading = true
            )
        }

        scope.launch(Dispatchers.IO) {
            repository.getItem(todoItemId).collect { result ->
                result.onFailure {
                    _state.update {
                        it.copy(
                            loading = false,
                            failed = true,
                            errorMessage = "Проблемы с соединением"
                        )
                    }
                }
                result.onSuccess { data ->
                    todoItem = data
                    _state.update {
                        it.copy(
                            loading = false,
                            failed = false,
                            text = todoItem.text,
                            importance = todoItem.importance,
                            hasDeadline = todoItem.deadline != null,
                            deadline = todoItem.deadline ?: Date().time
                        )
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

        scope.launch(Dispatchers.IO) {
            val deadline = if (state.value.hasDeadline) {
                state.value.deadline
            } else null

            val newTodoItem = TodoItem(
                id = todoItemId,
                text = state.value.text,
                importance = state.value.importance,
                deadline = deadline,
                isDone = todoItem.isDone,
                createdAt = todoItem.createdAt,
                changedAt = Date().time
            )

            repository.addOrUpdateItem(newTodoItem).collect { result ->
                result.onFailure {
                    _state.update {
                        it.copy(
                            loading = false
                        )
                    }
                    _effect.emit(
                        ItemScreenSideEffect.ShowSnackBarSideEffect("Не удалось сохранить")
                    )
                }
                result.onSuccess {
                    _effect.emit(ItemScreenSideEffect.LeaveScreenSideEffect)
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

        scope.launch(Dispatchers.IO) {
            repository.deleteItem(todoItem.id).collect { result ->
                result.onFailure {
                    _state.update {
                        it.copy(
                            loading = false
                        )
                    }
                    _effect.emit(
                        ItemScreenSideEffect.ShowSnackBarSideEffect("Не удалось удалить")
                    )
                }
                result.onSuccess {
                    _effect.emit(ItemScreenSideEffect.LeaveScreenSideEffect)
                }
            }
        }
    }

    private fun updateText(text: String) {
        _state.update {
            it.copy(text = text)
        }
    }

    private fun updatePriority(importance: Importance) {
        _state.update {
            it.copy(importance = importance)
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