package com.example.secretdiary.di

//import com.example.secretdiary.di.HeaderInterceptor
import com.example.secretdiary.di.interceptor.RedirectInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SecretDiaryObject {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            //.addInterceptor(HeaderInterceptor())
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