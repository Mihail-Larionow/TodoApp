package com.michel.core.data.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.michel.core.data.BuildConfig
import com.michel.core.data.interactor.TodoItemsInteractor
import com.michel.core.data.repository.LocalTodoItemsRepository
import com.michel.core.data.repository.RemoteTodoItemsRepository
import com.michel.core.data.repository.TokenRepository
import com.michel.database.data.TodoItemsDatabase
import com.michel.network.api.backend.TodoItemsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * WorkManager that periodically loads data from the server into local db
 */
class TodoItemsWorkManager(
    private val appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    // DI не захотел работать, поэтому пришлось ручками прописывать... лайк за старания...
    override suspend fun doWork(): Result {

        val tokenRepository = TokenRepository(appContext)
        val todoItemsApi = TodoItemsApi(BuildConfig.TODOAPP_BASE_URL)
        val remoteRepository =
            RemoteTodoItemsRepository(tokenRepo = tokenRepository, api = todoItemsApi)

        val database = TodoItemsDatabase.createDataBase(appContext)
        val localRepository = LocalTodoItemsRepository(database)

        val interactor = TodoItemsInteractor(local = localRepository, remote = remoteRepository)

        withContext(Dispatchers.IO) {
            interactor.loadTodoItems()
        }

        return Result.success()
    }

}