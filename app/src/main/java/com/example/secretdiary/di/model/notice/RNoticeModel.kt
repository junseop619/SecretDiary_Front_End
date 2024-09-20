package com.example.secretdiary.di.model.notice

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.Date

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
    val noticeImgPath: String?,

    @SerializedName("date")
    val date: String
)
