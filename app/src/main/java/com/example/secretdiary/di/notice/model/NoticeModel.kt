package com.example.secretdiary.di.notice.model

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class NoticeModel(
    @SerializedName("noticeId")
    val noticeId: Long,

    @SerializedName("userEmail")
    val userEmail: String,

    @SerializedName("noticeTitle")
    val noticeTitle: String,

    @SerializedName("noticeText")
    val noticeText: String,

    @SerializedName("noticeImg")
    val noticeImg: MultipartBody.Part
)
