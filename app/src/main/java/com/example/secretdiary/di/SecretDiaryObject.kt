package com.example.secretdiary.di

import com.example.secretdiary.di.interceptor.RedirectInterceptor
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object SecretDiaryObject {

    private const val BASE_URL = "http://10.0.2.2:8080/"


    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(RedirectInterceptor())
            .build()
    }

    private val getRetrofitSD by lazy {
        Retrofit.Builder()
            .baseUrl(SecretDiaryObject.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }

    val getRetrofitSDService : SecretDiaryAPI by lazy { getRetrofitSD.create(SecretDiaryAPI::class.java) }

    fun getInstance(): SecretDiaryAPI? {
        return getRetrofitSDService
    }
}
