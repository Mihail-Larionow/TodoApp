package com.michel.core.data.repository

import com.michel.common.di.Dispatcher
import com.michel.common.di.TodoDispatchers
import com.michel.core.data.datasource.local.LocalDataSource
import com.michel.core.data.datasource.remote.RemoteDataSource
import com.michel.core.data.models.TodoItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class WorkerRepositoryImpl @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
    @Dispatcher(TodoDispatchers.IO) private val dispatcherIO: CoroutineDispatcher
) : WorkerRepository {

    // Синхронизирует данные на сервере и устройстве с возвращением результата
    override suspend fun synchronize(): Result<Unit> = withContext(dispatcherIO) {
        val result = remote.getAll()
        result.onFailure { throwable ->
            return@withContext Result.failure(throwable)
        }
        result.onSuccess { remoteItems ->
            mergeData(remoteItems)
        }
        return@withContext Result.success(Unit)
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
    private suspend fun loadTodoItems(items: List<TodoItem>) {
        items.forEach { local.addOrUpdateItem(it) }
    }

    // Мержит список из локальной бд со списком на сервере
    // После чего, загружает итоговый список в локальную бд
    private suspend fun sendTodoItems(todoItems: List<TodoItem>) {
        val result = remote.updateAll(todoItems)
        result.onFailure { throw it }
        result.onSuccess { items ->
            items.forEach { local.addOrUpdateItem(it) }
        }
    }

}