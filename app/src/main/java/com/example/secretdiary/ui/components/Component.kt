package com.example.secretdiary.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.secretdiary.R
import com.example.secretdiary.ui.theme.lightBlue

enum class SecurityNav(val title: String){
    Login(title = "login"),
    Join(title = "join")
}

sealed class BottomNavItem(
    val title: Int, val icon: Int, val screenRoute: String
) {
    object Home : BottomNavItem(R.string.Home, R.drawable.ic_home, "home")
    object Friend : BottomNavItem(R.string.Friend, R.drawable.ic_person, "friend")
    object Setting : BottomNavItem(R.string.Setting, R.drawable.ic_settings, "setting")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    TopAppBar(
        title = {
            Text(
                text = when (currentRoute) {
                    BottomNavItem.Setting.screenRoute -> "내 설정"
                    BottomNavItem.Friend.screenRoute -> "내 친구"
                    else -> "Secret Diary"
                }
            )
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = lightBlue,
            titleContentColor = Color.Black
        ),
        navigationIcon = {
            if (currentRoute != BottomNavItem.Home.screenRoute &&
                currentRoute != BottomNavItem.Friend.screenRoute &&
                currentRoute != BottomNavItem.Setting.screenRoute) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            when (currentRoute) {
                BottomNavItem.Home.screenRoute -> {
                    IconButton(onClick = {
                        navController.navigate("add_notice")
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_notice),
                            contentDescription = "Home Action"
                        )
                    }
                }
            }
        }
    )
}







