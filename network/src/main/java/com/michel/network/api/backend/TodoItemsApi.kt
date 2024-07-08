package com.michel.network.api.backend

import android.util.Log
import com.michel.network.api.backend.interceptors.HeaderInterceptor
import com.michel.network.api.backend.retrofit.RetrofitApi
import com.michel.network.api.dto.SingleTodoItemDto
import com.michel.network.api.dto.ListOfElementsDto
import com.michel.network.api.dto.TodoItemDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Named

/**
 * Implements logic to work with a server
 */
class TodoItemsApi @Inject constructor(
    @Named("URL") private val baseUrl: String
) {

    private lateinit var api: RetrofitApi
    private var revision = 0

    // Установка токена
    fun setToken(token: String) {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            //.addInterceptor(RetryInterceptor())
            .addInterceptor(loggingInterceptor)
            .addInterceptor(HeaderInterceptor(token))
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

        api = retrofit.create(RetrofitApi::class.java)
    }

    // Получить все таски с бекенда
    suspend fun getAll(): List<TodoItemDto> {
        val result = api.getItemsList()
        revision = result.revision
        return result.list
    }

    // Получить одну таску по айди
    suspend fun getItem(id: String): TodoItemDto {
        val result = api.getItem(id)
        revision = result.revision
        return result.element
    }

    // Удалить таску
    suspend fun deleteItem(id: String): TodoItemDto {
        val result = api.deleteItem(id = id, revision = revision)
        Log.i("backend", "$result")
        revision = result.revision
        return result.element
    }

    // Обновить таску
    suspend fun updateItem(todoItem: TodoItemDto): TodoItemDto {
        val result = api.updateItem(
            element = SingleTodoItemDto(todoItem),
            id = todoItem.id,
            revision = revision
        )
        Log.i("backend", "$result")
        revision = result.revision
        return result.element
    }

    // Добавить таску
    suspend fun addItem(todoItem: TodoItemDto): TodoItemDto {
        val result = api.addItem(element = SingleTodoItemDto(todoItem), revision = revision)
        Log.i("backend", "$result")
        revision = result.revision
        return result.element
    }

    // Добавить таску
    suspend fun updateAll(todoItems: List<TodoItemDto>): List<TodoItemDto> {
        val list = ListOfElementsDto(todoItems)
        val result = api.updateItemsList(list = list, revision = revision)
        Log.i("backend", "$result")
        revision = result.revision
        return result.list
    }

}