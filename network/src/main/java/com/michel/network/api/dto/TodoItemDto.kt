package com.michel.network.api.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class TodoItem to work with a server
 */
@Serializable
data class TodoItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("importance")
    val importance: String,
    @SerializedName("deadline")
    val deadline: Long? = null,
    @SerializedName("done")
    val isDone: Boolean,
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("changed_at")
    val changedAt: Long? = null,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("last_updated_by")
    val lastUpdatedBy: String = "device-id"
)
