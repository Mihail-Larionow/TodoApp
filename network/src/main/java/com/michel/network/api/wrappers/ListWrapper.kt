package com.michel.network.api.wrappers

import com.google.gson.annotations.SerializedName
import com.michel.network.api.dto.TodoItemDto
import kotlinx.serialization.Serializable

/**
 * Data class that used in requests and responses with list of elements
 */
@Serializable
data class ListWrapper(
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("list")
    val list: List<TodoItemDto>,
    @SerializedName("revision")
    val revision: Int? = null
)
