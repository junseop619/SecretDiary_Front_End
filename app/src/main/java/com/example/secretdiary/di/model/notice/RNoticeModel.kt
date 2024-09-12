package com.example.secretdiary.di.model.notice

import com.google.gson.annotations.SerializedName

data class RNoticeModel(
    @SerializedName("noticeId")
    val noticeId: Long,

    @SerializedName("userEmail")
    val userEmail: String,

    @SerializedName("noticeTitle")
    val noticeTitle: String,

    @SerializedName("noticeText")
    val noticeText: String,

    @SerializedName("noticeImgPath")
    val noticeImgPath: String?
)
