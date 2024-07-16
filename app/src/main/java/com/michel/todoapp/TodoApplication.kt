package com.michel.todoapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.michel.common.utils.NetworkState
import com.michel.todoworker.TodoItemsWorker
import com.michel.core.data.worker.TodoItemsWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * [Application] for TodoApp
 */
@HiltAndroidApp
class TodoApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: TodoItemsWorkerFactory

    @Inject
    lateinit var networkData: NetworkState

    /**
     * Workers configuration.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Network callback when the connection state is changed.
     */
    private val networkCallback: ConnectivityManager.NetworkCallback
        get() = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkData.setState(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                networkData.setState(false)
            }
        }

    override fun onCreate() {
        super.onCreate()
        startConnectivityObserving()
        startWorker()
    }

    private fun startWorker() {
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            TodoItemsWorker.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            TodoItemsWorker.periodicWorkRequest()
        )
    }

    private fun startConnectivityObserving() {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

}