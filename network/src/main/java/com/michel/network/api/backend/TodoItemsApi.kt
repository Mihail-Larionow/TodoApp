package com.michel.network.api.backend

import com.michel.network.api.backend.interceptors.HeaderInterceptor
import com.michel.network.api.backend.retrofit.RetrofitApi
import com.michel.network.api.dto.TodoItemDto
import com.michel.network.api.wrappers.ItemWrapper
import com.michel.network.api.wrappers.ListWrapper
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.cancellation.CancellationException

const val MAX_RETRIES_COUNT = 2
const val RETRY_INTERVAL = 2000L

/**
 * Implements logic to work with a server
 */
class TodoItemsApi @Inject constructor(
    @Named("URL") private val baseUrl: String
) {

    private lateinit var api: RetrofitApi
    private val revision: AtomicInteger = AtomicInteger(0)

    // Установка токена
    fun setToken(token: String) {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
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
        val response = makeRequest { api.getItemsList() }
        updateRevision(response.revision)
        return response.list
    }

    // Получить одну таску по айди
    suspend fun getItem(id: String): TodoItemDto {
        val response = makeRequest { api.getItem(id) }
        updateRevision(response.revision)
        return response.element
    }

    // Удалить таску
    suspend fun deleteItem(id: String): TodoItemDto {
        val response = makeRequest { api.deleteItem(id = id, revision = revision.get()) }
        updateRevision(response.revision)
        return response.element
    }

    // Обновить таску
    suspend fun updateItem(todoItem: TodoItemDto): TodoItemDto {
        val response = makeRequest {
            api.updateItem(
                element = ItemWrapper(element = todoItem),
                id = todoItem.id,
                revision = revision.get()
            )
        }
        updateRevision(response.revision)
        return response.element
    }

    // Добавить таску
    suspend fun addItem(todoItem: TodoItemDto): TodoItemDto {
        val response = makeRequest {
            api.addItem(element = ItemWrapper(element = todoItem), revision = revision.get())
        }
        updateRevision(response.revision)
        return response.element
    }

    // Добавить таску
    suspend fun updateAll(todoItems: List<TodoItemDto>): List<TodoItemDto> {
        val response = makeRequest {
            api.updateItemsList(list = ListWrapper(list = todoItems), revision = revision.get())
        }
        updateRevision(response.revision)
        return response.list
    }

    private suspend fun <T> makeRequest(request: suspend () -> Response<T>): T {
        try {
            return makeRetryingRequest(
                request = request,
                retriesCount = MAX_RETRIES_COUNT,
                interval = RETRY_INTERVAL
            ).body()!!
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (httpException: HttpException) {
            handleClientExceptions(httpException)
        } catch (socketException: SocketTimeoutException) {
            throw socketException
        } catch (unknownHostException: UnknownHostException) {
            throw unknownHostException
        } catch (ioException: IOException) {
            throw ioException
        }
        throw Exception("unknown")
    }

    private suspend fun <T> makeRetryingRequest(
        request: suspend () -> Response<T>,
        retriesCount: Int,
        interval: Long
    ): Response<T> {
        repeat(retriesCount) {
            try {
                val response = withTimeout(interval) {
                    request.invoke()
                }
                if (response.isSuccessful) return response
            } catch (httpException: HttpException) {
                handleServerExceptions(httpException)
            } catch (_: SocketTimeoutException) {
                throw SocketTimeoutException()
            } catch (_: TimeoutCancellationException) {
                throw SocketTimeoutException()
            }
        }
        throw SocketTimeoutException()
    }

    private fun handleServerExceptions(httpException: HttpException) {
        when (httpException.code()) {
            500 -> {

            }

            else -> throw httpException
        }
    }

    private fun handleClientExceptions(httpException: HttpException) {
        val exceptionCode = httpException.code()
        throw when (exceptionCode) {
            400 -> Exception("400")
            401 -> Exception("401")
            404 -> Exception("404")
            else -> Exception("unknown exception")
        }
    }

    private fun updateRevision(revision: Int?) {
        if (revision != null) this.revision.set(revision)
    }

}