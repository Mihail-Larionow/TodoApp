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

@HiltViewModel
internal class TodoListScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val interactor: TodoItemsInteractor
) : ViewModel() {

    private var wasConnected = true
    private val connectivityReceiver = ConnectivityReceiver()

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
    val state: StateFlow<TodoListScreenState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<ListScreenSideEffect> = MutableSharedFlow()
    val effect: SharedFlow<ListScreenSideEffect> = _effect.asSharedFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    init {
        startWorkManager(context)
        startConnectionObserving(context)
        startDataObserving()
    }

    internal fun onEvent(event: ListScreenIntent) {
        try {
            handleEvent(event)
        } catch (_: Exception) {
            onFailure()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopConnectionObserving(context)
        Log.i("ui", "stopped")
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
            .addTag(
                "TodoAppWork" + System.currentTimeMillis()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "TodoAppWork",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    private fun startConnectionObserving(context: Context) {
        val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        context.registerReceiver(connectivityReceiver, filter)

        scope.launch(Dispatchers.IO) {
            collectConnection()
        }
    }

    private fun startDataObserving() {
        scope.launch(Dispatchers.IO) {
            collectData()
        }
    }

    private fun stopConnectionObserving(context: Context) {
        context.unregisterReceiver(connectivityReceiver)
    }

    //
    private suspend fun collectConnection() {
        connectivityReceiver.connection.collect {
            handleConnectionChanges(it)
        }
    }

    // Подписывается на получение данных
    private suspend fun collectData() {
        interactor.todoItemsList.collect { list ->
            todoItems = list.toMutableList()
            _state.update {
                it.copy(todoItems = todoItems)
            }
            updateCounter()
        }
    }

    // Обновляет счетчик выполненных тасок
    private fun updateCounter() {
        val doneItemsCount = todoItems.count { it.isDone }
        _state.update {
            it.copy(
                doneItemsCount = doneItemsCount,
            )
        }
    }

    // Обновляет состояние чекбокса глазика
    private fun updateVisibility(isNotVisible: Boolean) {
        _state.update {
            it.copy(
                doneItemsHide = isNotVisible
            )
        }
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
            result.onFailure {
                _state.update {
                    it.copy(
                        loading = false,
                        enabled = true,
                        failed = true,
                        errorMessage = "Проблемы с соединением"
                    )
                }
            }
            result.onSuccess {
                _state.update {
                    it.copy(
                        loading = false,
                        enabled = true,
                        failed = false,
                    )
                }
                updateCounter()
            }
        }
    }

    // Обновляет состояние таски
    private fun updateItem(updatedItem: TodoItem) {
        _state.update {
            it.copy(
                enabled = false,
            )
        }

        scope.launch(Dispatchers.IO) {
            val result = interactor.updateTodoItem(updatedItem)

            updateCounter()

            result.onFailure {
                updateCounter()
                _effect.emit(
                    ListScreenSideEffect.ShowSnackBarSideEffect("Не удалось сохранить")
                )
            }
            _state.update {
                it.copy(
                    enabled = true,
                )
            }
        }
    }

    // Удаляет таску
    private fun deleteItem(deletedItem: TodoItem) {
        _state.update {
            it.copy(
                enabled = false,
            )
        }

        updateCounter()

        scope.launch(Dispatchers.IO) {
            val result = interactor.deleteTodoItem(deletedItem)
            result.onFailure {
                updateCounter()
                _effect.emit(
                    ListScreenSideEffect.ShowSnackBarSideEffect("Не удалось удалить")
                )
            }
            _state.update {
                it.copy(
                    enabled = true,
                )
            }
        }
    }

    // Обновление всех тасок
    private fun updateItems(items: List<TodoItem>) {
        scope.launch(Dispatchers.IO) {
            val result = interactor.updateAllTodoItems(items)
            result.onFailure {
                updateCounter()
                _effect.emit(
                    ListScreenSideEffect.ShowSnackBarSideEffect("Синхронизация не удалась")
                )
            }
        }
    }

    // Обрабатывает изменение интернет соединения
    private suspend fun handleConnectionChanges(state: Boolean) {
        Log.i("ui", "changed")
        if(state && !wasConnected) {
            loadItems()
            wasConnected = true
            _effect.emit(ListScreenSideEffect.ShowSnackBarSideEffect("Подключен"))
        } else if(!state && wasConnected) {
            wasConnected = false
            _effect.emit(ListScreenSideEffect.ShowSnackBarSideEffect("Отключен"))
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
            is ToItemScreenIntent -> {}
        }
    }

    // Обработка ошбики
    private fun onFailure() {
        _state.update {
            it.copy(
                failed = true,
                enabled = false,
                errorMessage = "Неизвестная ошибка"
            )
        }
    }
}