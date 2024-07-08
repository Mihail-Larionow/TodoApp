package com.michel.core.data.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

/**
 * Receiving connection changes
 */
class ConnectivityReceiver : BroadcastReceiver() {

    private val _connection: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val connection: StateFlow<Boolean> = _connection

    // Срабатывает, когда изменяется интернет соединение
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            updateConnectionState(isConnected(context))
        }
    }

    // Обновляет записанное состояние соединения
    private fun updateConnectionState(state: Boolean) {
        _connection.update { state }
    }

    // Проверяет подключено ли устройство к интернету
    private fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}