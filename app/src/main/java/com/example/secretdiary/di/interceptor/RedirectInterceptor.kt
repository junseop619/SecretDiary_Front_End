package com.example.secretdiary.di.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RedirectInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        var response: Response = chain.proceed(request)
        var redirectCount = 0

        while (response.isRedirect && redirectCount < 5) {
            val location = response.header("Location") ?: break
            request = request.newBuilder().url(location).build()
            response = chain.proceed(request)
            redirectCount++
        }

        if (redirectCount >= 5) {
            throw java.net.ProtocolException("Too many redirects: $redirectCount")
        }

        return response
    }
}
