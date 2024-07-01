package com.michel.network.api

import android.util.Log
import com.michel.network.api.dto.ImportanceDto
import com.michel.network.api.dto.TodoItemDto
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.Date
import javax.inject.Inject

// Типо бекенд с хардкод датой
// Чтобы симмулировать работа с интернетом поставил задержку в 2 сек.
// Также с вероятностью 1 к 6 может выкинуться ошибка :)
class BackendApi @Inject constructor() {

    // Получить все таски с бекенда
    suspend fun getAll(): List<TodoItemDto> {
        delay(2000)
        val rnd = (0..5).random()
        Log.i("backend", "${rnd != 4}: ${todoItems.joinToString(" ")}")
        if (rnd != 4) return todoItems
        else throw IOException()
    }

    // Получить одну таску по айди
    suspend fun getItem(id: String): TodoItemDto? {
        delay(2000)
        val rnd = (0..5).random()
        Log.i("backend", "${rnd != 4}: ${todoItems.find { it.id == id }.toString()}")
        if (rnd != 4) return todoItems.find { it.id == id }
        else throw IOException()
    }

    // Удалить таску
    suspend fun deleteItem(id: String): Boolean {
        delay(2000)
        val item = todoItems.find { it.id == id }
        val rnd = (0..5).random()
        Log.i("backend", "(${rnd != 4}) item deleted: $id")
        if (item != null && rnd != 4) todoItems.remove(item)
        else if (rnd == 4) throw IOException()
        return true
    }

    // Добавить или обновить таску
    suspend fun addOrUpdateItem(todoItem: TodoItemDto): Boolean {
        delay(2000)
        val rnd = (0..5).random()
        Log.i("backend", "(${rnd != 4}) item updated: $todoItem")
        if (rnd != 4) {
            val item = todoItems.find { todoItem.id == it.id }
            if (item != null) {
                val index = todoItems.withIndex()
                    .first { todoItem.id == it.value.id }
                    .index
                todoItems[index] = todoItem
            } else {
                todoItems.add(todoItem)
            }
        } else throw IOException()
        return true
    }

    // Хардкод дата
    private val date: Long = 1
    private val todoItems = mutableListOf(
        TodoItemDto(
            id = "1",
            text = "Мега пж",
            importanceDto = ImportanceDto.High,
            isDone = false,
            deadline = 1718919593456 + 86400000,
            createdAt = date
        ),
        TodoItemDto(
            id = "2",
            text = "Поставьте максимум прошу",
            importanceDto = ImportanceDto.High,
            isDone = false,
            deadline = 1718919593456 + 86400000,
            createdAt = date
        ),
        TodoItemDto(
            id = "3",
            text = "Исправить все баги",
            importanceDto = ImportanceDto.High,
            isDone = false,
            deadline = 1718919593456 + 86400000,
            createdAt = date
        ),
        TodoItemDto(
            id = "4",
            text = "Тысячу раз задебажить это приложение",
            importanceDto = ImportanceDto.Low,
            isDone = true,
            deadline = 1718919593456,
            createdAt = date
        ),
        TodoItemDto(
            id = "5",
            text = "Сделать первое задание",
            importanceDto = ImportanceDto.High,
            isDone = true,
            createdAt = date
        ),
        TodoItemDto(
            id = "6",
            text = "Устроиться работать в пятерочку(",
            importanceDto = ImportanceDto.Low,
            isDone = false,
            createdAt = date
        ),
        TodoItemDto(
            id = "7",
            text = "Устроиться работать в Яндикс)",
            importanceDto = ImportanceDto.High,
            isDone = false,
            createdAt = date
        ),
        TodoItemDto(
            id = "8",
            text = "Выполненное задание",
            importanceDto = ImportanceDto.Low,
            isDone = true,
            createdAt = date
        ),
        TodoItemDto(
            id = "9",
            text = "Что-то важное",
            importanceDto = ImportanceDto.High,
            isDone = false,
            createdAt = date
        ),
        TodoItemDto(
            id = "10",
            text = "Что-то неважное",
            importanceDto = ImportanceDto.Low,
            isDone = false,
            createdAt = date
        ),
        TodoItemDto(
            id = "11",
            text = "Задание с дедлайном",
            importanceDto = ImportanceDto.Standard,
            isDone = false,
            deadline = Date().time,
            createdAt = date
        ),
        TodoItemDto(
            id = "12",
            text = "Очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень длинный текст",
            importanceDto = ImportanceDto.Standard,
            isDone = false,
            createdAt = date
        ),
        TodoItemDto(
            id = "13",
            text = "Вставьте текст",
            importanceDto = ImportanceDto.Standard,
            isDone = false,
            createdAt = date
        )
    )
}