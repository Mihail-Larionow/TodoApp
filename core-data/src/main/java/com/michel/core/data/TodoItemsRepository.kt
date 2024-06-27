package com.michel.core.data

import com.michel.core.data.mapper.toTodoItem
import com.michel.core.data.mapper.toTodoItemEntity
import com.michel.core.data.models.TodoItem
import com.michel.core.data.models.emptyTodoItem
import com.michel.core.data.utils.ResourceState
import com.michel.network.api.BackendApi
import com.michel.network.api.models.emptyTodoItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoItemsRepository @Inject constructor(
    private val api: BackendApi
) {

    // Возвращает все таски списком
    fun getAll(): Flow<ResourceState<List<TodoItem>>> {
        return flow {
            val resource = try {
                ResourceState.Success(
                    data = api.getAll().map {
                        it.toTodoItem()
                    }
                )
            } catch (e: Exception) {
                ResourceState.Failed("Ошибка соединения")
            }
            emit(resource)
        }
    }

    // Добавляет новую таску в список, либо заменяет таску с таким же id
     fun addOrUpdateItem(todoItem: TodoItem): Flow<ResourceState<Boolean>> {
        return flow {
            val result = try {
                ResourceState.Success(
                    data = api.addOrUpdateItem(todoItem.toTodoItemEntity())
                )
            } catch (e:Exception) {
                ResourceState.Failed("Не удалось сохранить")
            }
            emit(result)
        }
    }

    // Удаляет таску из списка
    fun deleteItem(id: String): Flow<ResourceState<Boolean>> {
        return flow {
            val result = try {
                ResourceState.Success(
                    data = api.deleteItem(id)
                )
            } catch (e: Exception) {
                ResourceState.Failed("Не удалось удалить")
            }
            emit(result)
        }
    }

    // Находит таску по id, либо возвращает пустую таску
    fun getItem(id: String): Flow<ResourceState<TodoItem>> {
        return flow {
            val resource = try {
                val item = api.getItem(id) ?: emptyTodoItemEntity()
                ResourceState.Success(
                    data = item.toTodoItem()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                ResourceState.Failed(e.message ?: "")
            }
            emit(resource)
        }
    }

}