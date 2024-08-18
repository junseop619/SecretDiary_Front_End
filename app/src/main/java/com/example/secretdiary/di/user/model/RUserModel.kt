package com.example.secretdiary.di.user.model

import com.google.gson.annotations.SerializedName

data class RUserModel(
    @SerializedName("userId")
    val userId: Long,

    @SerializedName("userEmail")
    val userEmail: String,

    @SerializedName("userNickName")
    val userNickName: String,

    @SerializedName("userText")
    val userText: String,

    @SerializedName("userImgPath")
    val userImgPath: String?



)
