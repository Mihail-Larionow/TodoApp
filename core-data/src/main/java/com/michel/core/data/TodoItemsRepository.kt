package com.michel.core.data

import com.michel.core.data.mappers.toTodoItem
import com.michel.core.data.mappers.toTodoItemEntity
import com.michel.core.data.models.TodoItem
import com.michel.network.api.BackendApi
import com.michel.network.api.dto.emptyTodoItemEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoItemsRepository @Inject constructor(
    private val api: BackendApi
) {

    // Возвращает все таски списком
    fun getAll(): Flow<Result<List<TodoItem>>> {
        return flow {
            val result = try {
                Result.success(
                    api.getAll().map {
                        it.toTodoItem()
                    }
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Result.failure(exception)
            }
            emit(result)
        }
    }

    // Добавляет новую таску в список, либо заменяет таску с таким же id
    fun addOrUpdateItem(todoItem: TodoItem): Flow<Result<Boolean>> {
        return flow {
            val result = try {
                Result.success(api.addOrUpdateItem(todoItem.toTodoItemEntity()))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Result.failure(exception)
            }
            emit(result)
        }
    }

    // Удаляет таску из списка
    fun deleteItem(id: String): Flow<Result<Boolean>> {
        return flow {
            val result = try {
                Result.success(api.deleteItem(id))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Result.failure(exception)
            }
            emit(result)
        }
    }

    // Находит таску по id, либо возвращает пустую таску
    fun getItem(id: String): Flow<Result<TodoItem>> {
        return flow {
            val resource = try {
                val item = api.getItem(id) ?: emptyTodoItemEntity()
                Result.success(item.toTodoItem())
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Result.failure(exception)
            }
            emit(resource)
        }
    }

}