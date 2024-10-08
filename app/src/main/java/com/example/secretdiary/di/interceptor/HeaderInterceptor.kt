package com.example.secretdiary.di.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("Content-Type", "multipart/form-data")
            .build()
        return chain.proceed(newRequest)
    }
}