package com.michel.feature.todolistscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michel.core.data.models.TodoItem
import com.michel.core.data.repository.IRepository
import com.michel.feature.todolistscreen.utils.ListScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenIntent.ToItemScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenSideEffect
import com.michel.feature.todolistscreen.utils.TodoListScreenState
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
import javax.inject.Inject

@HiltViewModel
class TodoListScreenViewModel @Inject constructor(
    private val repository: IRepository
) : ViewModel() {

    private var todoItems: MutableList<TodoItem> = mutableListOf()

    private val _state = MutableStateFlow(
        TodoListScreenState(
            todoItems = todoItems,
            doneItemsHide = false,
            doneItemsCount = 0,
            failed = false,
            loading = true,
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
        onEvent(ListScreenIntent.GetItemsIntent)
    }

    internal fun onEvent(event: ListScreenIntent) {
        try {
            when (event) {
                ListScreenIntent.GetItemsIntent -> getItems()
                is ListScreenIntent.ChangeVisibilityIntent -> updateVisibility(event.isNotVisible)
                is ListScreenIntent.DeleteItemIntent -> deleteItem(event.item)
                is ListScreenIntent.UpdateItemIntent -> updateItem(event.item)
                is ToItemScreenIntent -> {}
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

    // Достает все таски с репозитория
    private fun getItems() {
        _state.update {
            it.copy(
                loading = true
            )
        }

        scope.launch(Dispatchers.IO) {
            repository.getAll().collect { result ->
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
                    todoItems = data.toMutableList()
                    _state.update {
                        it.copy(
                            loading = false,
                            failed = false,
                            todoItems = todoItems
                        )
                    }
                    updateCounter()
                }
            }
        }
    }

    // Обновляет состояние таски
    private fun updateItem(updatedItem: TodoItem) {
        val index = todoItems.withIndex()
            .first{ updatedItem.id == it.value.id }
            .index
        val lastItemState = todoItems[index] // Так называемый backup

        todoItems[index] = updatedItem

        updateCounter()

        scope.launch(Dispatchers.IO) {
            repository.addOrUpdateItem(updatedItem).collect { result ->
                result.onFailure {
                    todoItems[index] = lastItemState
                    updateCounter()

                    _effect.emit(
                        ListScreenSideEffect.ShowSnackBarSideEffect("Не удалось сохранить")
                    )
                }
            }
        }
    }

    // Удаляет из репозитория таску
    private fun deleteItem(deletedItem: TodoItem) {
        val lastItemsState = todoItems.toMutableList() // Так называемый backup
        todoItems.remove(deletedItem)

        _state.update {
            it.copy(
                loading = true,
                todoItems = todoItems
            )
        }

        updateCounter()

        scope.launch(Dispatchers.IO) {
            repository.deleteItem(deletedItem.id).collect { result ->
                result.onFailure {
                    todoItems = lastItemsState
                    updateCounter()

                    _effect.emit(
                        ListScreenSideEffect.ShowSnackBarSideEffect("Не удалось удалить")
                    )
                }
                _state.update {
                    it.copy(
                        loading = false,
                        todoItems = todoItems
                    )
                }
            }
        }
    }
}