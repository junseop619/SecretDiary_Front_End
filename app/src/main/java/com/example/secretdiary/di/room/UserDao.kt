package com.example.secretdiary.di.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * from users WHERE userName = :userName")
    fun getUser(userName: String): Flow<User>

    @Query("SELECT userName FROM Users ORDER BY lastLoginTime DESC LIMIT 1")
    fun getMostRecentUserName(): String?

    @Query("DELETE FROM Users")
    fun clear()

}

