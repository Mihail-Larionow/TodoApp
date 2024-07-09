package com.michel.todoapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.michel.core.data.utils.TodoItemsWorker
import com.michel.core.data.utils.TodoItemsWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class... that's all...
 */
@HiltAndroidApp
class TodoApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: TodoItemsWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private val networkCallback: ConnectivityManager.NetworkCallback
        get() = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.i("worker", "connected")
                WorkManager
                    .getInstance(applicationContext)
                    .enqueue(TodoItemsWorker.oneTimeWorkRequest())
            }
        }

    override fun onCreate() {
        super.onCreate()
        startConnectivityObserving()
    }

    private fun startConnectivityObserving() {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            TodoItemsWorker.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            TodoItemsWorker.periodicWorkRequest()
        )

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

}