package com.example.secretdiary.di

import com.example.secretdiary.di.model.friend.FriendModel
import com.example.secretdiary.di.model.notice.RNoticeModel
import com.example.secretdiary.di.model.user.LoginModel
import com.example.secretdiary.di.model.user.RUserModel
import com.example.secretdiary.di.model.user.UserModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface SecretDiaryAPI {

    //user && security
    @POST("security/join") //client ->db
    suspend fun joinUser(@Body userModel: UserModel) : Response<Void>

    @POST("security/login")
    suspend fun loginUser(@Body loginModel: LoginModel): Response<String>

    @POST("security/autoLogin")
    suspend fun autoLogin(@Header("Authorization") token: String): Response<Void>

    @POST("security/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<String>

    @DELETE("delete/{userEmail}")
    suspend fun deleteUser(/*@Header("Authorization") token: String, */@Path("userEmail") userEmail: String): Response<ResponseBody>

    @Multipart
    @PUT("security/update/{userEmail}")
    suspend fun updateUser(
        @Path("userEmail") userEmail: String,
        @Part("userNickName") userNickname: String,
        @Part("userText") userText: String,
        @Part userImg: MultipartBody.Part
    ): Response<ResponseBody>


    @Multipart
    @POST("upload")
    suspend fun upload(
        @Part("userEmail") userEmail: String,
        @Part("noticeTitle") noticeTitle: String,
        @Part("noticeText") noticeText: String,
        @Part noticeImage: MultipartBody.Part
    ): Response<ResponseBody>

    @GET("read/notice/user")
    suspend fun readUserNotice(@Query("userEmail") query: String): Response<List<RNoticeModel>>

    @GET("search/notice")
    suspend fun searchNotice(@Query("keyword") query: String): Response<List<RNoticeModel>>

    @GET("read/detail/notice")
    suspend fun readDetailNotice(@Query("noticeId") query: Long): Response<RNoticeModel>

    @GET("security/user/{userEmail}")
    suspend fun userInfo(@Path("userEmail") userEmail: String): Response<RUserModel>

    @GET("search/{keyword}/{userEmail}")
    suspend fun searchUser(@Path("keyword") keyword: String,  @Path("userEmail") userEmail: String): Response<List<RUserModel>>

    @GET("friend/check/{userEmail}/{friendEmail}")
    suspend fun checkFriend(@Path("userEmail") userEmail: String, @Path("friendEmail") friendEmail: String): Response<Boolean>

    @GET("friend/request/check/{userEmail}/{friendEmail}")
    suspend fun checkRequest(@Path("userEmail") userEmail: String, @Path("friendEmail") friendEmail: String): Response<Boolean>


    @POST("friend/request/{userEmail}/{friendEmail}")
    suspend fun sendRequestFriend(@Path("userEmail") userEmail: String, @Path("friendEmail") friendEmail: String) : Response<Void>


    @GET("friend/request/list/{userEmail}")
    suspend fun friendRequestList(@Path("userEmail") userEmail: String): Response<List<FriendModel>>

    @POST("friend/accept/{userEmail}/{friendEmail}")
    suspend fun acceptFriendRequest(@Path("userEmail") userEmail: String, @Path("friendEmail") friendEmail: String) : Response<Void>


    @GET("friend/my/{userEmail}")
    suspend fun getReadMyFriendList(@Path("userEmail") userEmail: String): Response<List<FriendModel>>

}