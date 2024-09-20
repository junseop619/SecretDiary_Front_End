package com.example.secretdiary.di

//import com.example.secretdiary.di.HeaderInterceptor
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

    /*
    // Custom Gson for LocalDateTime
    val gson = GsonBuilder()
        .registerTypeAdapter(
            LocalDateTime::class.java,
            JsonDeserializer { json: JsonElement, _, _ ->
                LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
            }
        )
        .registerTypeAdapter(
            LocalDateTime::class.java,
            JsonSerializer { src: LocalDateTime, _, _ ->
                JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")))
            }
        )
        .setLenient()
        .create()

     */



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
            //.addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val getRetrofitSDService : SecretDiaryAPI by lazy { getRetrofitSD.create(SecretDiaryAPI::class.java) }

    fun getInstance(): SecretDiaryAPI? {
        return getRetrofitSDService
    }
}
