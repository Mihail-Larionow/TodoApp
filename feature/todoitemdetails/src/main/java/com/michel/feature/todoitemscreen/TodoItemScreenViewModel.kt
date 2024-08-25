package com.michel.feature.todoitemscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import com.michel.core.data.models.emptyTodoItem
import com.michel.core.data.repository.TodoItemsRepository
import com.michel.core.ui.extensions.toDateText
import com.michel.core.ui.viewmodel.ScreenIntent
import com.michel.core.ui.viewmodel.ViewModelBase
import com.michel.feature.todoitemscreen.utils.ItemScreenEffect
import com.michel.feature.todoitemscreen.utils.ItemScreenIntent
import com.michel.feature.todoitemscreen.utils.ItemScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for item screen
 */
@HiltViewModel
internal class TodoItemScreenViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModelBase<ItemScreenState, ItemScreenIntent, ItemScreenEffect>(ItemScreenState()) {

    private val todoItemId = savedStateHandle.get<String>("id") ?: ""
    private var todoItem: TodoItem = emptyTodoItem()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    init {
        if (todoItemId != "none") {
            loadItemInfo()
            setState { copy(deleteButtonEnabled = true) }
        }
    }

    // Обрабатывает приходящие интенты
    override fun handleIntent(intent: ScreenIntent) {
        when (intent) {
            ItemScreenIntent.GetItemInfoIntent -> loadItemInfo()
            is ItemScreenIntent.DeleteIntent -> delete()
            is ItemScreenIntent.SaveIntent -> saveItem()
            is ItemScreenIntent.SetDeadlineDateIntent -> updateDeadline(intent.deadline)
            is ItemScreenIntent.SetDeadlineStateIntent -> updateHasDeadline(intent.hasDeadline)
            is ItemScreenIntent.SetPriorityIntent -> updatePriority(intent.importance)
            is ItemScreenIntent.SetTextIntent -> updateText(intent.text)
            is ItemScreenIntent.SetDatePickerStateIntent -> updateDatePickerState(intent.isExpanded)
            is ItemScreenIntent.SetPriorityMenuStateIntent -> updatePriorityMenuState(intent.isExpanded)
            ItemScreenIntent.ToListScreenIntent -> leaveScreen()
        }
    }

    // Получение информации о TodoItem
    private fun loadItemInfo() {
        setState { copy(loading = true, enabled = false) }
        scope.launch(Dispatchers.IO) {
            val item = repository.getTodoItem(todoItemId)
            updateInfo(item)
        }
    }

    // Сохраняет таску в репозиторий
    private fun saveItem() {
        setState { copy(enabled = false) }
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
            updateItem(newTodoItem)
        } else {
            addNewItem(newTodoItem)
        }
    }

    // Добавление TodoItem
    private fun addNewItem(todoItem: TodoItem) {
        repository.addTodoItem(todoItem)
        setEffect { ItemScreenEffect.LeaveScreenEffect }
    }

    // Обновление TodoItem
    private fun updateItem(todoItem: TodoItem) {
        repository.updateTodoItem(todoItem)
        setEffect { ItemScreenEffect.LeaveScreenEffect }
    }

    // Удаляет таску из репозитория
    private fun delete() {
        setState { copy(enabled = false) }
        repository.deleteTodoItem(todoItem)
        setEffect { ItemScreenEffect.LeaveScreenEffect }
    }

    // Переход на главный экран
    private fun leaveScreen() {
        setEffect { ItemScreenEffect.LeaveScreenEffect }
    }

    private fun updateText(text: String) {
        setState { copy(text = text) }
    }

    private fun updatePriority(importance: Importance) {
        setState { copy(importance = importance) }
    }

    private fun updateHasDeadline(hasDeadline: Boolean) {
        setState { copy(hasDeadline = hasDeadline) }
    }

    private fun updateDeadline(deadline: Long) {
        setState {
            copy(
                deadline = deadline,
                deadlineDateText = deadline.toDateText()
            )
        }
    }

    private fun updatePriorityMenuState(isExpanded: Boolean) {
        setState { copy(priorityMenuExpanded = isExpanded) }
    }

    private fun updateDatePickerState(isExpanded: Boolean) {
        setState { copy(datePickerExpanded = isExpanded) }
    }

    // Обновление информации TodoItem
    private fun updateInfo(data: TodoItem) {
        todoItem = data
        val itemDeadline = data.deadline ?: Date().time
        val deadlineString = itemDeadline.toDateText()
        setState {
            copy(
                loading = false,
                enabled = true,
                failed = false,
                text = todoItem.text,
                importance = todoItem.importance,
                hasDeadline = todoItem.deadline != null,
                deadline = itemDeadline,
                deadlineDateText = deadlineString
            )
        }
    }

}