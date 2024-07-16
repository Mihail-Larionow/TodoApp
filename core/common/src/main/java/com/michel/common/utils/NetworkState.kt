package com.michel.common.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Contains information about the state
 * of the internet connection and communicates
 * it to the entire application.
 */
@Singleton
class NetworkState @Inject constructor() {

    /**
     * Mutable and immutable Internet connection state flow.
     */
    private val _state = MutableStateFlow(false)
    val state = _state.asStateFlow()

    /**
     * Sets the new value of the Internet connection status.
     * @param newState - boolean field where true is connected
     * and false is not.
     */
    fun setState(newState: Boolean) {
        _state.update {
            newState
        }
    }

}