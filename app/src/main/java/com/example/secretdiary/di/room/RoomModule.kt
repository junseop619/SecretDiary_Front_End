package com.example.secretdiary.di.room

import android.app.Application
import android.content.Context
import com.example.secretdiary.di.room.repository.OfflineUsersRepository
import com.example.secretdiary.di.room.repository.UsersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(context, UserDatabase::class.java, "user_database")
            .build()
    }

    @Provides
    fun provideUserDao(app: Application): UserDao {
        return UserDatabase.getDatabase(app).userDao()
    }

    @Provides
    fun provideUsersRepository(userDao: UserDao): UsersRepository {
        return OfflineUsersRepository(userDao)
    }
}

