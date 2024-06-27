package com.michel.core.data.utils

import com.michel.core.data.models.TodoItem
import java.lang.Exception

sealed class ResourceState<T>(
    val data: T? = null,
    val errorMessage: String? = null
) {
    class Success<T>(data: T?): ResourceState<T>(data = data)
    class Failed<T>(errorMessage: String): ResourceState<T>(errorMessage = errorMessage)
}