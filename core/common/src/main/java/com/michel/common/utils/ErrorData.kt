package com.michel.common.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Contains and redirects all exceptions
 * that occur in the application.
 */
@Singleton
class ErrorData @Inject constructor() {

    /**
     * Mutable and immutable exception flow.
     */
    private val _errors = MutableSharedFlow<Throwable>(replay = 1)
    val errors = _errors.asSharedFlow()

    /**
     * Emits exceptions into flow.
     * @param throwable - exception that`s emitted into flow.
     */
    fun emitError(throwable: Throwable) {
        _errors.tryEmit(throwable)
    }

}