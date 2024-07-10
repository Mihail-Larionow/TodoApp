package com.michel.core.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.michel.core.data.interactor.WorkerInteractor
import java.util.concurrent.TimeUnit

/**
 * Worker that periodically loads data from the server into local db
 */
class TodoItemsWorker(
    appContext: Context,
    params: WorkerParameters,
    private val interactor: WorkerInteractor,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val result = interactor.synchronize()
        result.onFailure { return Result.failure() }
        return Result.success()
    }

    companion object {
        const val TAG = "TodoItemsWorker"
        private const val INTERVAL = 8L
        private val CONSTRAINTS = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun oneTimeWorkRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<TodoItemsWorker>()
                .setConstraints(CONSTRAINTS)
                .build()
        }

        fun periodicWorkRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<TodoItemsWorker>(INTERVAL, TimeUnit.HOURS)
                .setConstraints(CONSTRAINTS)
                .build()
        }
    }

}