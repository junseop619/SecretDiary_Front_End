package com.example.secretdiary.di.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.Date

@Entity(tableName = "Users")
data class User(
    @PrimaryKey
    val userName: String,
    val autoLogin: Boolean,
    val lastLoginTime: Date
)
