package com.example.secretdiary.di.room.repository

import com.example.secretdiary.di.room.User
import com.example.secretdiary.di.room.UserDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineUsersRepository (
    private val userDao: UserDao
) : UsersRepository {
    override suspend fun insertUser(user: User) = userDao.insert(user)
    override suspend fun updateUser(user: User) = userDao.update(user)
    override suspend fun deleteUser(user: User) = userDao.delete(user)

    override fun getUserStream(userName: String): Flow<User?> = userDao.getUser(userName)
}