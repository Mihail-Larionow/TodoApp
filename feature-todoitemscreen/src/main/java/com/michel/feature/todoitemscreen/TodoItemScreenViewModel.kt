package com.michel.feature.todoitemscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.michel.core.data.interactor.TodoItemsInteractor
import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import com.michel.core.data.models.emptyTodoItem
import com.michel.core.ui.extensions.toDateText
import com.michel.core.ui.viewmodel.ScreenIntent
import com.michel.core.ui.viewmodel.ViewModelBase
import com.michel.feature.todoitemscreen.utils.ItemScreenIntent
import com.michel.feature.todoitemscreen.utils.ItemScreenEffect
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
    private val interactor: TodoItemsInteractor,
    savedStateHandle: SavedStateHandle
) : ViewModelBase<ItemScreenState, ItemScreenIntent, ItemScreenEffect>(ItemScreenState()) {

    private val todoItemId = savedStateHandle.get<String>("id") ?: ""
    private var todoItem: TodoItem = emptyTodoItem()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    init {
        Log.i("ui","created")
        if (todoItemId != "none") {
            handleIntent(ItemScreenIntent.GetItemInfoIntent)
            setState {
                copy(deleteButtonEnabled = true)
            }
        }
    }

    // Обрабатывает приходящие интенты
    override fun handleIntent(intent: ScreenIntent) {
        when (intent) {
            ItemScreenIntent.GetItemInfoIntent -> getItem()
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
    private fun getItem() {
        setState { copy(loading = true, enabled = false) }
        scope.launch(Dispatchers.IO) {
            val result = interactor.loadTodoItem(todoItemId)
            result.onFailure { onFail(state.value.errorMessage) }
            result.onSuccess { updateInfo(it) }
        }
    }

    // Сохраняет таску в репозиторий
    private fun saveItem() {
        setState { copy(enabled = false) }
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
                updateItem(newTodoItem)
            } else {
                addNewItem(newTodoItem)
            }
        }
    }

    // Добавление TodoItem
    private suspend fun addNewItem(todoItem: TodoItem) {
        val result = interactor.addTodoItem(todoItem)
        result.onFailure { onFail("Не удалось сохранить на сервере") }
        setEffect { ItemScreenEffect.LeaveScreenEffect }
    }

    // Обновление TodoItem
    private suspend fun updateItem(todoItem: TodoItem) {
        val result = interactor.updateTodoItem(todoItem)
        result.onFailure { onFail("Не удалось сохранить на сервере") }
        setEffect { ItemScreenEffect.LeaveScreenEffect }
    }

    // Удаляет таску из репозитория
    private fun delete() {
        setState { copy(enabled = false) }
        scope.launch(Dispatchers.IO) {
            val result = interactor.deleteTodoItem(todoItem)
            result.onFailure { onFail("Не удалось удалить на сервере") }
            setEffect{ ItemScreenEffect.LeaveScreenEffect }
        }
    }

    // Переход на главный экран
    private fun leaveScreen() {
        scope.launch(Dispatchers.IO) {
            setEffect{ ItemScreenEffect.LeaveScreenEffect }
        }
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

    // Обработка ошибки
    private fun onFail(message: String) {
        setState { copy(loading = false, enabled = false) }
        setEffect{ ItemScreenEffect.ShowSnackBarEffect(message) }
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