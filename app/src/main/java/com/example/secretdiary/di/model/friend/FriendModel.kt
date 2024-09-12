package com.example.secretdiary.di.model.friend

import com.google.gson.annotations.SerializedName

data class FriendModel(
    @SerializedName("userEmail")
    val userEmail: String,

    @SerializedName("friendEmail")
    val friendEmail: String
)
