package com.michel.core.data.interactor

import com.michel.core.data.models.TodoItem
import com.michel.core.data.repository.LocalTodoItemsRepository
import com.michel.core.data.repository.RemoteTodoItemsRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

/**
 * Combines both (remote and local) repositories
 */
@Singleton
class TodoItemsInteractor @Inject constructor(
    private val remote: RemoteTodoItemsRepository,
    private val local: LocalTodoItemsRepository
) {
    // Флоу список тасок, которые находятся в локальной бд
    val todoItemsList: Flow<List<TodoItem>> = local.todoItems

    // Загружает таски из интернета в локальную бд (перед этим очищая ее)
    // Зачем очистка? Ну чтобы синхронизировать таски на устройстве с сервером
    suspend fun loadTodoItems(): Result<List<TodoItem>> {
        return try {
            val items = remote.getAll()
            items.forEach { local.addOrUpdate(it) }
            Result.success(items)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    // Загружает одну таску из интернета в локальную бд
    suspend fun loadTodoItem(todoItemId: String): Result<TodoItem> {
        return try {
            val item = remote.getItem(todoItemId)
            local.addOrUpdate(item)
            Result.success(item)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    // Обновляет таску в локальной бд и на сервере
    suspend fun updateTodoItem(todoItem: TodoItem): Result<TodoItem> {
        return try {
            local.addOrUpdate(todoItem)
            val item = remote.updateItem(todoItem)
            Result.success(item)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    // Мержит список из локальной бд со списком на сервере
    // После чего, загружает итоговый список в локальную бд
    suspend fun updateAllTodoItems(todoItems: List<TodoItem>): Result<List<TodoItem>> {
        return try {
            val items = remote.updateAll(todoItems)
            items.forEach { local.addOrUpdate(it) }
            Result.success(items)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    // Добавляет таску в локальную бд и на сервер
    suspend fun addTodoItem(todoItem: TodoItem): Result<TodoItem> {
        return try {
            val itemWithId = todoItem.copy(id = UUID.randomUUID().toString())
            local.addOrUpdate(itemWithId)
            val item = remote.addItem(itemWithId)
            Result.success(item)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    // Удаляет таску из локальной бд и на сервере
    suspend fun deleteTodoItem(todoItem: TodoItem): Result<TodoItem> {
        return try {
            local.deleteItem(todoItem)
            val item = remote.deleteItem(todoItem.id)
            Result.success(item)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}