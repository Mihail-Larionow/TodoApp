package com.michel.network.api.backend.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Retries request on error (didn't work)
 */
internal class RetryInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try{
            return retry(chain)
        } catch (exception: Exception) {
            exception.printStackTrace()
            return retry(chain)
        }
    }

    private fun retry(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.i("backend", "retry $request")
        val response = chain.proceed(request)
        return response
    }
}