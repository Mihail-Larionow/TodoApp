package com.michel.feature.todolistscreen

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.michel.core.data.models.TodoItem
import com.michel.core.data.repository.TodoItemsRepository
import com.michel.core.ui.viewmodel.ScreenIntent
import com.michel.core.ui.viewmodel.ViewModelBase
import com.michel.feature.todolistscreen.utils.ListScreenEffect
import com.michel.feature.todolistscreen.utils.ListScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenIntent.ToItemScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

/**
 * ViewModel for items list screen
 */
@HiltViewModel
internal class TodoListScreenViewModel @Inject constructor(
    private val repository: TodoItemsRepository
) : ViewModelBase<ListScreenState, ListScreenIntent, ListScreenEffect>(ListScreenState()) {

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    init {
        startDataObserving()
        startErrorsObserving()
    }

    // Обработка поступившего интента
    override fun handleIntent(intent: ScreenIntent) {
        when (intent) {
            ListScreenIntent.GetItemsIntent -> loadItems()
            ListScreenIntent.StartLoadingIntent -> startRefreshing()
            is ListScreenIntent.ChangeVisibilityIntent -> updateVisibility(intent.isNotVisible)
            is ListScreenIntent.DeleteItemIntent -> deleteItem(intent.item)
            is ListScreenIntent.UpdateItemIntent -> updateItem(intent.item)
            is ListScreenIntent.UpdateItemsIntent -> loadItems()
            is ToItemScreenIntent -> leaveScreen(intent.id)
        }
    }

    // Запуск отслеживания данных
    private fun startDataObserving() {
        scope.launch {
            combine(state, repository.getTodoItemsFlow()) { state, list ->
                ListScreenState(
                    doneItemsHide = state.doneItemsHide,
                    doneItemsCount = list.count { it.isDone },
                    todoItems = list,
                    isRefreshing = state.isRefreshing
                )
            }.collect {
                setState { it }
            }
        }
    }

    // Запуск отслеживания ошибок
    private fun startErrorsObserving() {
        scope.launch {
            repository.errors.collectLatest { throwable -> showSnackBar("${throwable.message}") }
        }
    }

    // Запускает рефреш данных
    private fun startRefreshing() {
        setState { copy(isRefreshing = true) }
    }

    // Обновляет состояние чекбокса глазика
    private fun updateVisibility(isNotVisible: Boolean) {
        setState { copy(doneItemsHide = isNotVisible) }
    }

    // Достает все таски
    private fun loadItems() {
        scope.launch(Dispatchers.IO) {
            setState { copy(failed = false) }
            val result = repository.synchronizeDataWithResult()

            result.onSuccess {
                onLoadSuccess()
            }
            result.onFailure { throwable ->
                onLoadFailure("${throwable.message}")
            }
        }
    }

    // Обновляет состояние таски
    private fun updateItem(updatedItem: TodoItem) {
        repository.updateTodoItem(updatedItem)
    }

    // Удаляет таску
    private fun deleteItem(deletedItem: TodoItem) {
        repository.deleteTodoItem(deletedItem)
    }

    // Перейти на экрана TodoItem с id
    private fun leaveScreen(itemId: String) {
        setEffect { ListScreenEffect.LeaveScreenEffect(itemId) }
    }

    private fun showSnackBar(message: String) {
        setEffect { ListScreenEffect.ShowSimpleSnackBarEffect(message) }
    }

    // Обработка ошбики при получении списка
    private fun onLoadFailure(message: String) {
        setState {
            copy(
                isRefreshing = false,
                failed = true,
                errorMessage = message
            )
        }
        setEffect { ListScreenEffect.ShowButtonSnackBarEffect(message, "ПОВТОРИТЬ") }
    }

    // Обработка удачного получения списка
    private fun onLoadSuccess() {
        setState {
            copy(
                isRefreshing = false,
                failed = false
            )
        }
    }

}