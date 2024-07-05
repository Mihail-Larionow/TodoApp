package com.michel.network.api.backend.interceptors

import okhttp3.Interceptor
import okhttp3.Response


internal class HeaderInterceptor(private val token: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "OAuth $token")
            .build()
        return chain.proceed(request)
    }
}