package com.michel.todoapp.todolistscreen

import androidx.lifecycle.ViewModel
import com.michel.core.date.TodoItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoItemsRepository
) : ViewModel() {

    val todoItemsList = repository.getAll()
}