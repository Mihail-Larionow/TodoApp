package com.michel.core.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.michel.core.data.interactor.WorkerInteractor
import javax.inject.Inject

class TodoItemsWorkerFactory @Inject constructor(
    private val interactor: WorkerInteractor
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            TodoItemsWorker::class.java.name ->
                TodoItemsWorker(appContext, workerParameters, interactor)
            else -> null
        }
    }

}