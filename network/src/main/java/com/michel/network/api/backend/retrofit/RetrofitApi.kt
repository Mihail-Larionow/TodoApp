package com.michel.network.api.backend.retrofit

import com.michel.network.api.dto.ElementDto
import com.michel.network.api.dto.ElementListDto
import com.michel.network.api.dto.ResElementDto
import com.michel.network.api.dto.TodoItemsListDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


internal interface RetrofitApi {

    @GET("list")
    suspend fun getItemsList(): TodoItemsListDto

    @GET("list/{id}")
    suspend fun getItem(
        @Path("id") id: String
    ): ResElementDto

    @POST("list")
    suspend fun addItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body element: ElementDto
    ): ResElementDto

    @PATCH("list")
    suspend fun updateItemsList(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body list: ElementListDto
    ): TodoItemsListDto

    @PUT("list/{id}")
    suspend fun updateItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body element: ElementDto,
        @Path("id") id: String
    ): ResElementDto

    @DELETE("list/{id}")
    suspend fun deleteItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): ResElementDto
}