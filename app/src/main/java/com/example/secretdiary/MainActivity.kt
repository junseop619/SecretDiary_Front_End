package com.example.secretdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.secretdiary.ui.home.HomeViewModel
import com.example.secretdiary.ui.home.MainScreen
import com.example.secretdiary.ui.security.LoginScreen
import com.example.secretdiary.ui.security.SecurityScreen
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
                SecurityScreen()
                /*
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController, viewModel = SecurityViewModel()) }
                    composable("main") { MainScreen(viewModel = HomeViewModel()) }
                }*/
                //LoginScreen(viewModel = SecurityViewModel())
            }
        }
    }
}

