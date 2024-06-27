package com.michel.network.api

import android.util.Log
import com.michel.network.api.models.PriorityEntity
import com.michel.network.api.models.TodoItemEntity
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackendApi @Inject constructor() {

    suspend fun getAll(): List<TodoItemEntity> {
        delay(2000)
        val rnd = (0..5).random()
        Log.i("backend", "${rnd != 4}: ${todoItems.joinToString(" ")}")
        if(rnd != 4) return todoItems
        else throw IOException()
    }

    suspend fun getItem(id: String): TodoItemEntity? {
        delay(2000)
        val rnd = (0..5).random()
        Log.i("backend", "${rnd != 4}: ${todoItems.find{ it.id == id }.toString()}")
        if(rnd != 4) return todoItems.find{ it.id == id }
        else throw IOException()
    }

    suspend fun deleteItem(id: String): Boolean {
        delay(2000)
        val item = todoItems.find{ it.id == id }
        val rnd = (0..5).random()
        Log.i("backend", "(${rnd != 4}) item deleted: $id")
        if(item != null && rnd != 4) todoItems.remove(item)
        else if(rnd == 4) throw IOException()
        return true
    }

    suspend fun addOrUpdateItem(todoItem: TodoItemEntity): Boolean {
        delay(2000)
        val rnd = (0..5).random()
        Log.i("backend", "(${rnd != 4}) item updated: $todoItem")
        if(rnd != 4){
            val item = todoItems.find { todoItem.id == it.id }
            if (item != null) {
                item.text = todoItem.text
                item.priorityEntity = todoItem.priorityEntity
                item.isDone = todoItem.isDone
                item.deadline = todoItem.deadline
                item.changedAt = todoItem.changedAt
            }
            else {
                todoItems.add(todoItem)
            }
        }
        else throw IOException()
        return true
    }

    // Хардкод дата
    private val date: Long = 1
    private val todoItems = mutableListOf(
        TodoItemEntity(
            id = "1",
            text = "Мега пж",
            priorityEntity = PriorityEntity.High,
            isDone = false,
            deadline = 1718919593456 + 86400000,
            createdAt = date
        ),
        TodoItemEntity(
            id = "2",
            text = "Поставьте максимум прошу",
            priorityEntity = PriorityEntity.High,
            isDone = false,
            deadline = 1718919593456 + 86400000,
            createdAt = date
        ),
        TodoItemEntity(
            id = "3",
            text = "Исправить все баги",
            priorityEntity = PriorityEntity.High,
            isDone = false,
            deadline = 1718919593456 + 86400000,
            createdAt = date
        ),
        TodoItemEntity(
            id = "4",
            text = "Тысячу раз задебажить это приложение",
            priorityEntity = PriorityEntity.Low,
            isDone = true,
            deadline = 1718919593456,
            createdAt = date
        ),
        TodoItemEntity(
            id = "5",
            text = "Сделать первое задание",
            priorityEntity = PriorityEntity.High,
            isDone = true,
            createdAt = date
        ),
        TodoItemEntity(
            id = "6",
            text = "Устроиться работать в пятерочку(",
            priorityEntity = PriorityEntity.Low,
            isDone = false,
            createdAt = date
        ),
        TodoItemEntity(
            id = "7",
            text = "Устроиться работать в Яндикс)",
            priorityEntity = PriorityEntity.High,
            isDone = false,
            createdAt = date
        ),
        TodoItemEntity(
            id = "8",
            text = "Выполненное задание",
            priorityEntity = PriorityEntity.Low,
            isDone = true,
            createdAt = date
        ),
        TodoItemEntity(
            id = "9",
            text = "Что-то важное",
            priorityEntity = PriorityEntity.High,
            isDone = false,
            createdAt = date
        ),
        TodoItemEntity(
            id = "10",
            text = "Что-то неважное",
            priorityEntity = PriorityEntity.Low,
            isDone = false,
            createdAt = date
        ),
        TodoItemEntity(
            id = "11",
            text = "Задание с дедлайном",
            priorityEntity = PriorityEntity.Standard,
            isDone = false,
            deadline = Date().time,
            createdAt = date
        ),
        TodoItemEntity(
            id = "12",
            text = "Очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень длинный текст",
            priorityEntity = PriorityEntity.Standard,
            isDone = false,
            createdAt = date
        ),
        TodoItemEntity(
            id = "13",
            text = "Вставьте текст",
            priorityEntity = PriorityEntity.Standard,
            isDone = false,
            createdAt = date
        )
    )
}