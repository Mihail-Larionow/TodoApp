package com.michel.core.data.interactor

import com.michel.core.data.models.TodoItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TodoItemsInteractor {

    val errors: Flow<Throwable>

    // Загружает одну таску из интернета в локальную бд
    suspend fun getTodoItem(todoItemId: String): TodoItem

    // Синхронизирует данные на сервере и устройстве с возвращением результата
    suspend fun synchronizeDataWithResult(): Result<Unit>

    // Синхронизирует данные на сервере и устройстве
    fun synchronizeData()

    // Возвращает флоу со списком тасок
    fun getTodoItemsFlow() : Flow<List<TodoItem>>

    // Обновляет таску в локальной бд и на сервере
    fun updateTodoItem(todoItem: TodoItem)

    // Добавляет таску в локальную бд и на сервер
    fun addTodoItem(todoItem: TodoItem)

    // Удаляет таску из локальной бд и на сервере
    fun deleteTodoItem(todoItem: TodoItem)

}