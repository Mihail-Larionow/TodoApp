package com.michel.network.api.backend.retrofit

import com.michel.network.api.dto.SingleTodoItemDto
import com.michel.network.api.dto.ListOfElementsDto
import com.michel.network.api.dto.SingleElementResponse
import com.michel.network.api.dto.ListOfElementsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit interface to make request on server
 */
internal interface RetrofitApi {

    @GET("list")
    suspend fun getItemsList(): ListOfElementsResponse

    @GET("list/{id}")
    suspend fun getItem(
        @Path("id") id: String
    ): SingleElementResponse

    @POST("list")
    suspend fun addItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body element: SingleTodoItemDto
    ): SingleElementResponse

    @PATCH("list")
    suspend fun updateItemsList(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body list: ListOfElementsDto
    ): ListOfElementsResponse

    @PUT("list/{id}")
    suspend fun updateItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body element: SingleTodoItemDto,
        @Path("id") id: String
    ): SingleElementResponse

    @DELETE("list/{id}")
    suspend fun deleteItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): SingleElementResponse

}