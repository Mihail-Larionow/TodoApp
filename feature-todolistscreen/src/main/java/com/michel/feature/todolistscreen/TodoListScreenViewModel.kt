package com.michel.feature.todolistscreen

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.michel.core.data.interactor.TodoItemsInteractor
import com.michel.core.data.models.TodoItem
import com.michel.core.ui.viewmodel.ScreenIntent
import com.michel.core.ui.viewmodel.ViewModelBase
import com.michel.feature.todolistscreen.utils.ListScreenEffect
import com.michel.feature.todolistscreen.utils.ListScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenIntent.ToItemScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

/**
 * ViewModel for items list screen
 */
@HiltViewModel
internal class TodoListScreenViewModel @Inject constructor(
    private val interactor: TodoItemsInteractor
) : ViewModelBase<ListScreenState, ListScreenIntent, ListScreenEffect>(ListScreenState()) {

    private var todoItems: MutableList<TodoItem> = mutableListOf()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    init {
        Log.i("ui","created")
        startDataObserving()
    }

    // Обработка поступившего интента
    override fun handleIntent(intent: ScreenIntent) {
        when (intent) {
            ListScreenIntent.GetItemsIntent -> loadItems()
            is ListScreenIntent.ChangeVisibilityIntent -> updateVisibility(intent.isNotVisible)
            is ListScreenIntent.DeleteItemIntent -> deleteItem(intent.item)
            is ListScreenIntent.UpdateItemIntent -> updateItem(intent.item)
            is ListScreenIntent.UpdateItemsIntent -> updateItems(intent.items)
            is ToItemScreenIntent -> leaveScreen(intent.id)
        }
    }

    // Запуск отслеживания данных
    private fun startDataObserving() {
        scope.launch(Dispatchers.IO) {
            combine(state, interactor.todoItemsList) { state, list ->
                ListScreenState(
                    doneItemsHide = state.doneItemsHide,
                    doneItemsCount = list.count { it.isDone },
                    todoItems = list,
                    isLoading = state.isLoading
                )
            }.collect {
                setState { it }
            }
            updateCounter()
        }
    }

    // Обновляет счетчик выполненных тасок
    private fun updateCounter() {
        val doneItemsCount = todoItems.count { it.isDone }
        setState { copy(doneItemsCount = doneItemsCount) }
    }

    // Обновляет состояние чекбокса глазика
    private fun updateVisibility(isNotVisible: Boolean) {
        setState { copy(doneItemsHide = isNotVisible) }
    }

    // Достает все таски
    private fun loadItems() {
        setState {
            copy(
                isLoading = true,
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
        setState { copy(enabled = false) }
        scope.launch(Dispatchers.IO) {
            val result = interactor.updateTodoItem(updatedItem)
            updateCounter()
            result.onFailure {
                showSnackBar("Не удалось сохранить на сервере")
            }
            setState { copy(enabled = true) }
        }
    }

    // Удаляет таску
    private fun deleteItem(deletedItem: TodoItem) {
        setState { copy(enabled = false) }
        updateCounter()
        scope.launch(Dispatchers.IO) {
            val result = interactor.deleteTodoItem(deletedItem)
            result.onFailure {
                showSnackBar("Не удалось удалить на сервере")
            }
            setState { copy(enabled = true) }
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
            result.onSuccess { setState { copy(enabled = true, failed = false) } }
        }
    }

    // Перейти на экрана TodoItem с id
    private fun leaveScreen(itemId: String) {
        setEffect { ListScreenEffect.LeaveScreenEffect(itemId) }
    }

    private fun showSnackBar(message: String) {
        setEffect { ListScreenEffect.ShowSnackBarEffect(message) }
    }

    // Обработка ошбики при получении списка
    private fun onGetAllFail(message: String) {
        setState {
            copy(
                isLoading = false,
                failed = true,
                enabled = false,
                errorMessage = message
            )
        }
    }

    // Обработка удачного получения списка
    private fun onGetAllSuccess() {
        setState {
            copy(
                isLoading = false,
                enabled = true,
                failed = false
            )
        }
        updateCounter()
    }

}