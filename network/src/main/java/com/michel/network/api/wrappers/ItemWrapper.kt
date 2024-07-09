package com.michel.network.api.wrappers

import com.google.gson.annotations.SerializedName
import com.michel.network.api.dto.TodoItemDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class that used in requests and responses with one element
 */
@Serializable
data class ItemWrapper(
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("element")
    val element: TodoItemDto,
    @SerializedName("revision")
    val revision: Int? = null
)
