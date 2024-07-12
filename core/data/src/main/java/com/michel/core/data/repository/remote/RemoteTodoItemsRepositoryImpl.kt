package com.michel.core.data.repository.remote

import com.michel.core.data.mappers.toTodoItem
import com.michel.core.data.mappers.toTodoItemDto
import com.michel.core.data.models.TodoItem
import com.michel.network.api.TodoItemsApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implements work with remote data sources
 */
@Singleton
class RemoteTodoItemsRepositoryImpl @Inject constructor(
    private val api: TodoItemsApi,
) : RemoteTodoItemsRepository {

    // Возвращает значение удаленной ревизии
    override fun getRevision(): Int? {
        return api.getRevision()
    }

    // Возвращает список тасок
    override suspend fun getAll(): Result<List<TodoItem>> = withExceptionHandling {
        api.getAll().map { it.toTodoItem() }
    }

    // Изменяет таску
    override suspend fun updateItem(todoItem: TodoItem): Result<TodoItem> = withExceptionHandling {
        api.updateItem(todoItem.toTodoItemDto()).toTodoItem()
    }

    // Добавляет таску
    override suspend fun addItem(todoItem: TodoItem): Result<TodoItem> = withExceptionHandling {
        api.addItem(todoItem.toTodoItemDto()).toTodoItem()
    }

    // Удаляет таску
    override suspend fun deleteItem(id: String): Result<TodoItem> = withExceptionHandling {
        api.deleteItem(id).toTodoItem()
    }

    // Возвращает таску
    override suspend fun getItem(id: String): Result<TodoItem> = withExceptionHandling {
        api.getItem(id).toTodoItem()
    }

    // Мержит все таски
    override suspend fun updateAll(todoItems: List<TodoItem>): Result<List<TodoItem>> =
        withExceptionHandling {
            api.updateAll(todoItems.map { it.toTodoItemDto() }).map { it.toTodoItem() }
        }

    // Выполняет действие с обработкой ошибок
    private suspend fun <T> withExceptionHandling(action: suspend () -> T): Result<T> = try {
        val result = action.invoke()
        Result.success(result)
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (exception: Exception) {
        Result.failure(exception)
    }

}