package com.michel.feature.todoitemscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michel.core.data.interactor.TodoItemsInteractor
import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import com.michel.core.data.models.emptyTodoItem
import com.michel.core.ui.extensions.toDateText
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

/**
 * ViewModel for item screen
 */
@HiltViewModel
internal class TodoItemScreenViewModel @Inject constructor(
    private val interactor: TodoItemsInteractor,
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
            deadlineDateText = (todoItem.deadline ?: Date().time).toDateText(),
            datePickerExpanded = false,
            priorityMenuExpanded = false,
            loading = todoItemId != "none",
            failed = false,
            enabled = todoItemId == "none",
            errorMessage = "Проблемы с соединением",
            deleteButtonEnabled = todoItemId != "none"
        )
    )
    internal val state: StateFlow<TodoItemScreenState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<ItemScreenSideEffect> = MutableSharedFlow()
    internal val effect: SharedFlow<ItemScreenSideEffect> = _effect.asSharedFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    init {
        if (state.value.loading) onEvent(ItemScreenIntent.GetItemInfoIntent)
    }

    internal fun onEvent(event: ItemScreenIntent) {
        try { handleEvent(event) } catch (_: Exception) { }
    }

    // Получение информации о TodoItem
    private fun getInfo() {
        _state.update { it.copy(loading = true, enabled = false) }
        scope.launch(Dispatchers.IO) {
            val result = interactor.loadTodoItem(todoItemId)
            result.onFailure { onFail(state.value.errorMessage) }
            result.onSuccess { updateInfo(it) }
        }
    }

    // Сохраняет таску в репозиторий
    private fun save() {
        _state.update { it.copy(enabled = false) }
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

            if (todoItemId != "none") {
                updateTodoItem(newTodoItem)
            } else {
                addTodoItem(newTodoItem)
            }
        }
    }

    // Добавление TodoItem
    private suspend fun addTodoItem(todoItem: TodoItem) {
        val result = interactor.addTodoItem(todoItem)
        result.onFailure { onFail("Не удалось сохранить на сервере") }
        _effect.emit(ItemScreenSideEffect.LeaveScreenSideEffect)
    }

    // Обновление TodoItem
    private suspend fun updateTodoItem(todoItem: TodoItem) {
        val result = interactor.updateTodoItem(todoItem)
        result.onFailure { onFail("Не удалось сохранить на сервере") }
        _effect.emit(ItemScreenSideEffect.LeaveScreenSideEffect)
    }

    // Удаляет таску из репозитория
    private fun delete() {
        _state.update { it.copy(enabled = false) }
        scope.launch(Dispatchers.IO) {
            val result = interactor.deleteTodoItem(todoItem)
            result.onFailure { onFail("Не удалось удалить на сервере") }
            _effect.emit(ItemScreenSideEffect.LeaveScreenSideEffect)
        }
    }

    // Переход на главный экран
    private fun leaveScreen() {
        scope.launch(Dispatchers.IO) {
            _effect.emit(ItemScreenSideEffect.LeaveScreenSideEffect)
        }
    }

    private fun updateText(text: String) {
        _state.update { it.copy(text = text) }
    }

    private fun updatePriority(importance: Importance) {
        _state.update { it.copy(importance = importance) }
    }

    private fun updateHasDeadline(hasDeadline: Boolean) {
        _state.update { it.copy(hasDeadline = hasDeadline) }
    }

    private fun updateDeadline(deadline: Long) {
        _state.update {
            it.copy(
                deadline = deadline,
                deadlineDateText = deadline.toDateText()
            )
        }
    }

    private fun updatePriorityMenuState(isExpanded: Boolean) {
        _state.update { it.copy(priorityMenuExpanded = isExpanded) }
    }

    private fun updateDatePickerState(isExpanded: Boolean) {
        _state.update { it.copy(datePickerExpanded = isExpanded) }
    }

    // Обработка поступившего интента
    private fun handleEvent(event: ItemScreenIntent) {
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
            ItemScreenIntent.ToListScreenIntent -> leaveScreen()
        }
    }

    // Обработка ошибки
    private suspend fun onFail(message: String) {
        _state.update { it.copy(loading = false, enabled = true) }
        _effect.emit(ItemScreenSideEffect.ShowSnackBarSideEffect(message))
    }

    // Обновление информации TodoItem
    private fun updateInfo(data: TodoItem) {
        todoItem = data
        _state.update {
            it.copy(
                loading = false,
                enabled = true,
                failed = false,
                text = todoItem.text,
                importance = todoItem.importance,
                hasDeadline = todoItem.deadline != null,
                deadline = todoItem.deadline ?: Date().time
            )
        }
    }

}