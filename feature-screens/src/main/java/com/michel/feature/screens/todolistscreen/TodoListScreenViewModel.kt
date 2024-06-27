package com.michel.feature.screens.todolistscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michel.core.data.TodoItemsRepository
import com.michel.core.data.models.TodoItem
import com.michel.core.data.utils.ResourceState
import com.michel.feature.screens.todolistscreen.utils.ListScreenEffect
import com.michel.feature.screens.todolistscreen.utils.ListScreenEvent
import com.michel.feature.screens.todolistscreen.utils.ListScreenEvent.ToItemScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListScreenViewModel @Inject constructor(
    private val repository: TodoItemsRepository
): ViewModel() {

    private var todoItems: MutableList<TodoItem> = mutableListOf()

    private val _state = MutableStateFlow(
        ListScreenState(
            todoItems = todoItems,
            doneItemsHide = false,
            doneItemsCount = 0,
            failed = false,
            loading = true,
            errorMessage = ""
        )
    )
    val state: StateFlow<ListScreenState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<ListScreenEffect> = MutableSharedFlow()
    val effect: SharedFlow<ListScreenEffect> = _effect.asSharedFlow()

    init{
        onEvent(ListScreenEvent.GetItemsEvent)
    }

    internal fun onEvent(event: ListScreenEvent) {
        viewModelScope.launch {
            try{
                when(event) {
                    ListScreenEvent.GetItemsEvent -> getItems()
                    is ListScreenEvent.ChangeVisibilityEvent -> updateVisibility(event.isNotVisible)
                    is ListScreenEvent.DeleteItemEvent -> deleteItem(event.item)
                    is ListScreenEvent.UpdateItemEvent -> updateItem(event.item)
                    is ToItemScreenEvent -> { }
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

    private fun updateState() {
        val doneItemsCount = todoItems.count { it.isDone }

        _state.update {
            it.copy(
                doneItemsCount = doneItemsCount,
                todoItems = todoItems
            )
        }
    }

    private fun updateVisibility(isNotVisible: Boolean) {
        _state.update {
            it.copy(doneItemsHide = isNotVisible)
        }
    }

    private fun getItems() {
        _state.update {
            it.copy(
                loading = true
            )
        }

        viewModelScope.launch {
            repository.getAll().collect { res ->
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
                        _state.update {
                            it.copy(
                                loading = false,
                                failed = false
                            )
                        }
                        todoItems = res.data!!.toMutableList()
                        updateState()
                    }
                }
            }
        }
    }

    private fun updateItem(updatedItem: TodoItem) {
        val item = todoItems.find { updatedItem.id == it.id }
        val itemState = item!!.isDone

        item.isDone = updatedItem.isDone

        updateState()

        viewModelScope.launch {
            repository.addOrUpdateItem(updatedItem).collect { res ->
                when(res) {
                    is ResourceState.Failed -> {
                        item.isDone = itemState
                        updateState()

                        _effect.emit(
                            ListScreenEffect.ShowSnackBarEffect(res.errorMessage!!)
                        )
                    }
                    is ResourceState.Success -> { }
                }
            }
        }
    }

    // Удаляет из репозитория таску
    private fun deleteItem(item: TodoItem) {
        todoItems.remove(item)

        updateState()

        viewModelScope.launch {
            repository.deleteItem(item.id).collect { res ->
                when(res) {
                    is ResourceState.Failed -> {
                        todoItems.add(item)
                        updateState()

                        _effect.emit(
                            ListScreenEffect.ShowSnackBarEffect(res.errorMessage!!)
                        )
                    }
                    is ResourceState.Success -> { }
                }
            }
        }
    }
}

data class ListScreenState(
    val todoItems: List<TodoItem>,
    val doneItemsHide: Boolean,
    val doneItemsCount: Int,
    val failed: Boolean,
    val loading: Boolean,
    val errorMessage: String
)