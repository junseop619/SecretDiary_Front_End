package com.example.secretdiary.di

import com.example.secretdiary.di.notice.model.NoticeModel
import com.example.secretdiary.di.notice.model.RNoticeModel
import com.example.secretdiary.di.user.model.LoginModel
import com.example.secretdiary.di.user.model.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface SecretDiaryAPI {

    //user && security
    @POST("security/join") //client ->db
    suspend fun joinUser(@Body userModel: UserModel) : Response<Void>

    @POST("security/login")
    suspend fun loginUser(@Body loginModel: LoginModel): Response<Void>

    @GET("home")
    suspend fun userInfo(@Query("userId") userId:String): Call<List<UserModel>>

    //단일 image notice
    @Multipart
    @POST("upload")
    suspend fun upload(
        @Part("userEmail") userEmail: String,
        @Part("noticeTitle") noticeTitle: String,
        @Part("noticeText") noticeText: String,
        @Part noticeImage: MultipartBody.Part
    ): Response<ResponseBody>

    @GET("findAll2")
    fun readAll(): Call<List<RNoticeModel>>


    @GET("search/notice")
    fun search2(@Query("keyword") query: String): Call<List<RNoticeModel>>


}