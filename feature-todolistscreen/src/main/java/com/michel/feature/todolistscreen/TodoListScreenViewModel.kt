package com.michel.feature.todolistscreen

import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.michel.core.data.interactor.TodoItemsInteractor
import com.michel.core.data.models.TodoItem
import com.michel.core.data.utils.ConnectivityReceiver
import com.michel.core.data.utils.TodoItemsWorkManager
import com.michel.feature.todolistscreen.utils.ListScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenIntent.ToItemScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenSideEffect
import com.michel.feature.todolistscreen.utils.TodoListScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for items list screen
 */
@HiltViewModel
internal class TodoListScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val interactor: TodoItemsInteractor
) : ViewModel() {

    private val connectivityReceiver = ConnectivityReceiver()
    private var wasConnected = true

    private var todoItems: MutableList<TodoItem> = mutableListOf()

    private val _state = MutableStateFlow(
        TodoListScreenState(
            todoItems = todoItems,
            doneItemsHide = false,
            doneItemsCount = 0,
            failed = false,
            loading = true,
            enabled = false,
            errorMessage = ""
        )
    )
    internal val state: StateFlow<TodoListScreenState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<ListScreenSideEffect> = MutableSharedFlow()
    internal val effect: SharedFlow<ListScreenSideEffect> = _effect.asSharedFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    init {
        startWorkManager(context)
        startConnectionObserving(context)
        startDataObserving()
    }

    override fun onCleared() {
        super.onCleared()
        stopConnectionObserving(context)
    }

    //
    internal fun onEvent(event: ListScreenIntent) {
        try {
            handleEvent(event)
        } catch (_: Exception) {
            onGetAllFail("Неизвестная ошибка")
        }
    }

    // Запуск WorkManager
    private fun startWorkManager(context: Context) {
        val request = PeriodicWorkRequestBuilder<TodoItemsWorkManager>(
            repeatInterval = 8,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED,
                    requiresStorageNotLow = true
                )
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "TodoAppWork",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    // Запуск отслеживания интернет соединения
    private fun startConnectionObserving(context: Context) {
        val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        context.registerReceiver(connectivityReceiver, filter)
        scope.launch(Dispatchers.IO) { collectConnection() }
    }

    // Запуск отслеживания данных
    private fun startDataObserving() {
        scope.launch(Dispatchers.IO) { collectData() }
    }

    // Остановка отслеживания интернет соединения
    private fun stopConnectionObserving(context: Context) {
        context.unregisterReceiver(connectivityReceiver)
    }

    // Подписка на изменение интернета
    private suspend fun collectConnection() {
        connectivityReceiver.connection.collect { handleConnectionChanges(it) }
    }

    // Подписка на получение данных
    private suspend fun collectData() {
        interactor.todoItemsList.collect { list ->
            todoItems = list.toMutableList()
            _state.update { it.copy(todoItems = todoItems) }
            updateCounter()
        }
    }

    // Обновляет счетчик выполненных тасок
    private fun updateCounter() {
        val doneItemsCount = todoItems.count { it.isDone }
        _state.update { it.copy(doneItemsCount = doneItemsCount) }
    }

    // Обновляет состояние чекбокса глазика
    private fun updateVisibility(isNotVisible: Boolean) {
        _state.update { it.copy(doneItemsHide = isNotVisible) }
    }

    // Достает все таски
    private fun loadItems() {
        _state.update {
            it.copy(
                loading = true,
                enabled = false,
                failed = false
            )
        }

        scope.launch(Dispatchers.IO) {
            val result = interactor.loadTodoItems()
            result.onFailure { onGetAllFail("Не удалось получить данные") }
            result.onSuccess { onGetAllSuccess() }
        }
    }

    // Обновляет состояние таски
    private fun updateItem(updatedItem: TodoItem) {
        _state.update { it.copy(enabled = false) }
        scope.launch(Dispatchers.IO) {
            val result = interactor.updateTodoItem(updatedItem)
            updateCounter()
            result.onFailure {
                showSnackBar("Не удалось сохранить на сервере")
            }
            _state.update { it.copy(enabled = true) }
        }
    }

    // Удаляет таску
    private fun deleteItem(deletedItem: TodoItem) {
        _state.update { it.copy(enabled = false) }
        updateCounter()
        scope.launch(Dispatchers.IO) {
            val result = interactor.deleteTodoItem(deletedItem)
            result.onFailure {
                showSnackBar("Не удалось удалить на сервере")
            }
            _state.update { it.copy(enabled = true) }
        }
    }

    // Обновление всех тасок
    private fun updateItems(items: List<TodoItem>) {
        scope.launch(Dispatchers.IO) {
            val result = interactor.updateAllTodoItems(items)
            result.onFailure {
                updateCounter()
                showSnackBar("Синхронизация не удалась")
            }
            result.onSuccess { _state.update { it.copy(enabled = true, failed = false) } }
        }
    }

    // Перейти на экрана TodoItem с id
    private fun leaveScreen(itemId: String) {
        scope.launch(Dispatchers.IO) {
            _effect.emit(ListScreenSideEffect.LeaveScreenSideEffect(itemId))
        }
    }

    // Обрабатывает изменение интернет соединения
    private suspend fun handleConnectionChanges(state: Boolean) {
        Log.i("ui", "changed")
        if (state && !wasConnected) {
            updateItems(todoItems)
            wasConnected = true
            showSnackBar("Соединение установлено")
        } else if (!state && wasConnected) {
            wasConnected = false
            showSnackBar("Соединение потеряно")
        }
    }

    // Обработка поступившего интента
    private fun handleEvent(event: ListScreenIntent) {
        when (event) {
            ListScreenIntent.GetItemsIntent -> loadItems()
            is ListScreenIntent.ChangeVisibilityIntent -> updateVisibility(event.isNotVisible)
            is ListScreenIntent.DeleteItemIntent -> deleteItem(event.item)
            is ListScreenIntent.UpdateItemIntent -> updateItem(event.item)
            is ListScreenIntent.UpdateItemsIntent -> updateItems(event.items)
            is ToItemScreenIntent -> leaveScreen(event.id)
        }
    }

    private suspend fun showSnackBar(message: String) {
        _effect.emit(ListScreenSideEffect.ShowSnackBarSideEffect(message))
    }

    // Обработка ошбики при получении списка
    private fun onGetAllFail(message: String) {
        _state.update {
            it.copy(
                loading = false,
                failed = true,
                enabled = false,
                errorMessage = message
            )
        }
    }

    // Обработка удачного получения списка
    private fun onGetAllSuccess() {
        _state.update {
            it.copy(
                loading = false,
                enabled = true,
                failed = false
            )
        }
        updateCounter()
    }

}