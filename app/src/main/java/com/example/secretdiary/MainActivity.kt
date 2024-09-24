package com.example.secretdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.ui.platform.LocalContext
import com.example.secretdiary.di.room.UserDatabase
import com.example.secretdiary.di.room.repository.OfflineUsersRepository
import com.example.secretdiary.di.room.repository.UsersRepository
import com.example.secretdiary.ui.components.SDScreen
import com.example.secretdiary.ui.components.SDScreen
import com.example.secretdiary.ui.security.SecurityViewModel
import com.example.secretdiary.ui.theme.SecretDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecretDiaryTheme {
                val context = LocalContext.current
                val userDao = UserDatabase.getDatabase(context).userDao()
                val usersRepository: UsersRepository = OfflineUsersRepository(userDao)
                SDScreen(viewModel = SecurityViewModel(context, usersRepository))
                
            }
        }
    }
}

