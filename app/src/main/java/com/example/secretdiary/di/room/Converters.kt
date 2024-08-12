package com.example.secretdiary.di.room

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*


class Converters {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    @TypeConverter
    fun fromDate(date: Date?): String? {
        return date?.let { dateFormat.format(it) }
    }

    @TypeConverter
    fun toDate(dateString: String?): Date? {
        return dateString?.let { dateFormat.parse(it) }
    }
}