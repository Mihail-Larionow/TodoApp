package com.michel.core.data.repository

import com.michel.core.data.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface IRepository {

    // Возвращает все таски списком
    fun getAll(): Flow<Result<List<TodoItem>>>

    // Добавляет новую таску в список, либо заменяет таску с таким же id
    fun addOrUpdateItem(todoItem: TodoItem): Flow<Result<Boolean>>

    // Удаляет таску из списка
    fun deleteItem(id: String): Flow<Result<Boolean>>

    // Находит таску по id, либо возвращает пустую таску
    fun getItem(id: String): Flow<Result<TodoItem>>

}