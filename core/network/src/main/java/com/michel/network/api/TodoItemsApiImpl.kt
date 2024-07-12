package com.michel.network.api

import android.content.Context
import com.michel.network.R
import com.michel.network.backend.interceptors.HeaderInterceptor
import com.michel.network.backend.retrofit.RetrofitApi
import com.michel.network.dto.TodoItemDto
import com.michel.network.wrappers.ItemWrapper
import com.michel.network.wrappers.ListWrapper
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


/**
 * Implements logic to work with a server
 */
class TodoItemsApiImpl @Inject constructor(
    private val context: Context,
    baseUrl: String,
    token: String
) : TodoItemsApi {

    private val api: RetrofitApi
    private val revision: AtomicInteger = AtomicInteger(-1)


    init {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(READ_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectionPool(
                ConnectionPool(
                    maxIdleConnections = MAX_IDLE_CONNECTIONS_COUNT,
                    keepAliveDuration = KEEP_ALIVE_DURATION_MINUTES,
                    timeUnit = TimeUnit.MINUTES
                )
            )
            .addInterceptor(loggingInterceptor)
            .addInterceptor(HeaderInterceptor(token))
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

        api = retrofit.create(RetrofitApi::class.java)
    }

    // Возвращает значение ревизии c бекенда
    override fun getRevision(): Int? {
        return revision.get().let {
            if (it == -1) null
            else it
        }
    }

    // Получить все таски с бекенда
    override suspend fun getAll(): List<TodoItemDto> {
        val response = makeRequest { api.getItemsList() }
        updateRevision(response.revision)
        return response.list
    }

    // Получить одну таску по айди
    override suspend fun getItem(id: String): TodoItemDto {
        val response = makeRequest { api.getItem(id) }
        updateRevision(response.revision)
        return response.element
    }

    // Удалить таску
    override suspend fun deleteItem(id: String): TodoItemDto {
        val response = makeRequest { api.deleteItem(id = id, revision = revision.get()) }
        updateRevision(response.revision)
        return response.element
    }

    // Обновить таску
    override suspend fun updateItem(todoItem: TodoItemDto): TodoItemDto {
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
    override suspend fun addItem(todoItem: TodoItemDto): TodoItemDto {
        val response = makeRequest {
            api.addItem(element = ItemWrapper(element = todoItem), revision = revision.get())
        }
        updateRevision(response.revision)
        return response.element
    }

    // Добавить таску
    override suspend fun updateAll(todoItems: List<TodoItemDto>): List<TodoItemDto> {
        val response = makeRequest {
            api.updateItemsList(list = ListWrapper(list = todoItems), revision = revision.get())
        }
        updateRevision(response.revision)
        return response.list
    }

    // Выполняет запрос с надстройками
    private suspend fun <T> makeRequest(request: suspend () -> Response<T>): T {
        try {
            return makeRetryingRequest(
                request = request,
                retriesCount = MAX_RETRIES_COUNT,
                interval = RETRY_INTERVAL
            ).body()!!
        } catch (_: TimeoutCancellationException) {
            throw Exception(context.getString(R.string.exception_socket))
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (httpException: HttpException) {
            handleClientExceptions(httpException)
        } catch (_: SocketTimeoutException) {
            throw SocketTimeoutException(context.getString(R.string.exception_socket))
        } catch (_: UnknownHostException) {
            throw UnknownHostException(context.getString(R.string.exception_no_host))
        } catch (_: IOException) {
            throw IOException(context.getString(R.string.exception_io))
        }
        throw Exception(context.getString(R.string.exception_unknown))
    }

    // Выполняет запрос с возможность ретрая (например, при неверной ревизии или при ошибки на сервере)
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
                else {
                    throw HttpException(response)
                }
            } catch (httpException: HttpException) {
                val isLastRetry = it + 1 == MAX_RETRIES_COUNT
                handleHttpExceptions(exception = httpException, isLastRetry = isLastRetry)
            }
        }
        throw SocketTimeoutException()
    }

    private suspend fun handleHttpExceptions(exception: HttpException, isLastRetry: Boolean) {
        when (exception.code()) {
            500 -> {
                if (isLastRetry) throw exception
            }

            400 -> {
                if (isLastRetry) throw exception
                updateRevision(api.getItemsList().body()?.revision)
            }

            else -> throw exception
        }
    }

    private fun handleClientExceptions(httpException: HttpException) {
        val exceptionCode = httpException.code()
        throw when (exceptionCode) {
            400 -> Exception(context.getString(R.string.exception_400))
            401 -> Exception(context.getString(R.string.exception_401))
            404 -> Exception(context.getString(R.string.exception_404))
            500 -> Exception(context.getString(R.string.exception_500))
            else -> Exception(context.getString(R.string.exception_unknown))
        }
    }

    private fun updateRevision(revision: Int?) {
        if (revision != null) this.revision.set(revision)
    }

}