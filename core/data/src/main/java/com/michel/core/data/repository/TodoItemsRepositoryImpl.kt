package com.michel.core.data.repository

import com.michel.common.di.ApplicationScope
import com.michel.common.utils.ErrorData
import com.michel.core.data.datasource.local.LocalDataSource
import com.michel.core.data.datasource.remote.RemoteDataSource
import com.michel.core.data.models.TodoItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID
import javax.inject.Inject

/**
 * Combines both (remote and local) repositories
 */
internal class TodoItemsRepositoryImpl @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
    private val error: ErrorData,
    @ApplicationScope appScope: CoroutineScope,
) : TodoItemsRepository {

    private val handler = CoroutineExceptionHandler { _, throwable -> error.emitError(throwable) }

    private val scope = appScope + handler

    // Возвращает одну таску по itemId из локальной бд
    override suspend fun getTodoItem(todoItemId: String): TodoItem {
        return local.getItem(todoItemId)
    }

    // Синхронизирует данные на сервере и устройстве с возвращением результата
    override suspend fun synchronizeDataWithResult(): Result<Unit> {
        val result = remote.getAll()
        result.onFailure { throwable ->
            return Result.failure(throwable)
        }
        result.onSuccess { remoteItems ->
            mergeData(remoteItems)
        }
        return Result.success(Unit)
    }

    // Возвращает флоу список тасок, которые находятся в локальной бд
    override fun getTodoItemsFlow(): Flow<List<TodoItem>> {
        return local.getAllItemsFlow()
    }

    // Синхронизирует данные на сервере и устройстве
    override fun synchronizeData() {
        scope.launch {
            val result = remote.getAll()
            result.onFailure { throwable ->
                throw throwable
            }
            result.onSuccess { remoteItems ->
                mergeData(remoteItems)
            }
        }
    }

    // Мержит данные
    private suspend fun mergeData(items: List<TodoItem>) {
        val remoteRevision = remote.getRevision() ?: 0
        val localRevision = local.getRevision()

        if (localRevision > remoteRevision) {
            val localItems = local.getAllItems()
            sendTodoItems(localItems)
        } else {
            loadTodoItems(items)
        }

        val updatedRemoteRevision = remote.getRevision() ?: 0
        local.setRevision(updatedRemoteRevision)
    }

    // Загружает таски из интернета в локальную бд
    private fun loadTodoItems(items: List<TodoItem>) {
        scope.launch(Dispatchers.IO) {
            val itemsId = items.map { it.id }
            val localItems = local.getAllItems()
            localItems.forEach {
                if (it.id !in itemsId) local.deleteItem(it)
                else {
                    local.addOrUpdateItem(it)
                }
            }
        }
    }

    // Мержит список из локальной бд со списком на сервере
    // После чего, загружает итоговый список в локальную бд
    private fun sendTodoItems(todoItems: List<TodoItem>) {
        scope.launch(Dispatchers.IO) {
            val result = remote.updateAll(todoItems)
            result.onFailure { throw it }
            result.onSuccess { items ->
                items.forEach { local.addOrUpdateItem(it) }
            }
        }
    }

    // Обновляет таску в локальной бд и на сервере
    override fun updateTodoItem(todoItem: TodoItem) {
        scope.launch(Dispatchers.IO) {
            val notUpdatedItem = local.getItem(todoItem.id)
            local.addOrUpdateItem(todoItem)
            withResult(onFailure = { local.addOrUpdateItem(notUpdatedItem) }) {
                remote.updateItem(todoItem)
            }
        }
    }

    // Добавляет таску в локальную бд и на сервер
    override fun addTodoItem(todoItem: TodoItem) {
        scope.launch(Dispatchers.IO) {
            val itemWithId = todoItem.copy(id = UUID.randomUUID().toString())
            local.addOrUpdateItem(itemWithId)
            withResult(onFailure = { local.deleteItem(itemWithId) }) {
                remote.addItem(itemWithId)
            }
        }
    }

    // Удаляет таску из локальной бд и на сервере
    override fun deleteTodoItem(todoItem: TodoItem) {
        scope.launch(Dispatchers.IO) {
            val notDeletedItem = todoItem.copy()
            local.deleteItem(todoItem)
            withResult(onFailure = { local.addOrUpdateItem(notDeletedItem) }) {
                remote.deleteItem(todoItem.id)
            }
        }
    }

    // Обработка результата
    private suspend fun <T> withResult(
        onFailure: suspend () -> Unit,
        call: suspend () -> Result<T>
    ) {
        val result = call.invoke()
        val localRevision = local.getRevision()
        local.setRevision(localRevision + 1)
        result.onFailure { throwable ->
            when (throwable) {
                is UnknownHostException -> {}
                is SocketTimeoutException -> {}
                else -> onFailure()
            }
            throw throwable
        }
        result.onSuccess {
            val remoteRevision = remote.getRevision() ?: 0
            local.setRevision(remoteRevision)
        }
    }
}