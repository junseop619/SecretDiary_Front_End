package com.example.secretdiary.di.room.repository

import com.example.secretdiary.di.room.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

    suspend fun insertUser(user: User)

    suspend fun updateUser(user: User)

    suspend fun  deleteUser(user: User)

    fun  getUserStream(userName: String): Flow<User?>

    suspend fun getMostRecentUserName(): String?

    fun clearRoom()
}