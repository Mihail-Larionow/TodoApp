package com.michel.feature.screens.todolistscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michel.core.data.TodoItemsRepository
import com.michel.core.data.models.TodoItem
import com.michel.feature.screens.todolistscreen.utils.ListScreenEvent
import com.michel.feature.screens.todolistscreen.utils.ListScreenEvent.ToItemScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoItemsRepository
): ViewModel() {

    private var todoItems: List<TodoItem> = emptyList()

    private val _state = MutableStateFlow(
        ListScreenState(
            todoItems = todoItems,
            doneItemsHide = false,
            doneItemsCount = 0
        )
    )

    val state: StateFlow<ListScreenState> = _state.asStateFlow()

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
            } catch (e: Exception) {

            }
        }
    }

    private fun updateList() {
        val doneItemsCount = todoItems.count { it.isDone }
        val updatedItems = todoItems

        _state.update {
            it.copy(
                todoItems = updatedItems,
                doneItemsCount = doneItemsCount
            )
        }
    }

    private fun updateVisibility(isNotVisible: Boolean) {
        _state.update {
            it.copy(doneItemsHide = isNotVisible)
        }
    }

    private fun getItems() {
        viewModelScope.launch {
            repository.getAll().collect {
                todoItems = it
                updateList()
            }
        }
    }

    private fun updateItem(item: TodoItem) {
        viewModelScope.launch {
            repository.addOrUpdateItem(item)
            updateList()
        }
    }

    // Удаляет из репозитория таску
    private fun deleteItem(item: TodoItem) {
        viewModelScope.launch {
            repository.deleteItem(todoItem = item)
            updateList()
        }
    }

}

data class ListScreenState(
    val todoItems: List<TodoItem>,
    val doneItemsHide: Boolean,
    val doneItemsCount: Int
)