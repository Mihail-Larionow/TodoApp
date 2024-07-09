package com.michel.network.api.backend.retrofit

import com.michel.network.api.wrappers.ItemWrapper
import com.michel.network.api.wrappers.ListWrapper
import retrofit2.Response
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
    suspend fun getItemsList(): Response<ListWrapper>

    @GET("list/{id}")
    suspend fun getItem(
        @Path("id") id: String
    ): Response<ItemWrapper>

    @POST("list")
    suspend fun addItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body element: ItemWrapper
    ): Response<ItemWrapper>

    @PATCH("list")
    suspend fun updateItemsList(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body list: ListWrapper
    ): Response<ListWrapper>

    @PUT("list/{id}")
    suspend fun updateItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body element: ItemWrapper,
        @Path("id") id: String
    ): Response<ItemWrapper>

    @DELETE("list/{id}")
    suspend fun deleteItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): Response<ItemWrapper>

}