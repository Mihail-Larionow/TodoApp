package com.michel.todoapp.todolistscreen

import androidx.lifecycle.ViewModel
import com.michel.core.date.TodoItemsRepository
import com.michel.core.date.models.TodoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoItemsRepository
) : ViewModel() {

    val screenState = ListScreenState(
        hideDoneItems = false,
        todoItems = repository.getAll()
    )

    // Удаляет из репозитория таску
    fun deleteItem(item: TodoItem){
        repository.deleteItem(todoItem = item)
    }

}

data class ListScreenState(
    var hideDoneItems: Boolean,
    val todoItems: List<TodoItem>
)