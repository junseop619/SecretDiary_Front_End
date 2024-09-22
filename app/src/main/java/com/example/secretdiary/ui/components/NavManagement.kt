package com.example.secretdiary.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.secretdiary.di.room.repository.UsersRepository
import com.example.secretdiary.ui.friend.FriendScreen
import com.example.secretdiary.ui.friend.FriendViewModel
import com.example.secretdiary.ui.friend.UserInfoScreen
import com.example.secretdiary.ui.friend.UserNoticeDetailScreen
import com.example.secretdiary.ui.home.AddNoticeScreen
import com.example.secretdiary.ui.home.HomeScreen
import com.example.secretdiary.ui.home.HomeViewModel
import com.example.secretdiary.ui.home.NoticeDetailScreen
import com.example.secretdiary.ui.security.JoinScreen
import com.example.secretdiary.ui.security.LoginScreen
import com.example.secretdiary.ui.security.SecurityScreen
import com.example.secretdiary.ui.security.SecurityViewModel
import com.example.secretdiary.ui.setting.SettingFirstTabScreen
import com.example.secretdiary.ui.setting.SettingScreen
import com.example.secretdiary.ui.setting.SettingViewModel
import com.example.secretdiary.ui.setting.UpdateUserScreen

@Composable
fun RootNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    usersRepository: UsersRepository
){
    val context = LocalContext.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ){
        navigation(startDestination = SecurityNav.Login.title, route = "securityNav"){

            composable(route = SecurityNav.Login.title){
                LoginScreen(
                    navController = navController,
                    viewModel = SecurityViewModel(context, usersRepository),
                    modifier = Modifier
                        .fillMaxSize()

                )
            }

            composable(route = SecurityNav.Join.title){
                JoinScreen(
                    navController = navController,
                    viewModel = SecurityViewModel(context, usersRepository),
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }

        navigation(startDestination = BottomNavItem.Home.screenRoute, route = "myNav"){

            composable(BottomNavItem.Home.screenRoute){
                HomeScreen(navController = navController, homeViewModel = HomeViewModel(usersRepository), componentViewModel = ComponentViewModel())
            }


            composable(BottomNavItem.Friend.screenRoute){
                FriendScreen(navController = navController, friendViewModel = FriendViewModel(usersRepository), componentViewModel = ComponentViewModel())
            }

            composable(BottomNavItem.Setting.screenRoute){
                SettingScreen(navController = navController, settingViewModel = SettingViewModel(usersRepository),componentViewModel = ComponentViewModel())
            }

            //home
            composable("add_notice"){
                AddNoticeScreen(navController = navController, viewModel = HomeViewModel(usersRepository))
            }

            composable(
                route = "home/{noticeId}",
                arguments = listOf(navArgument("noticeId") { type = NavType.LongType })
            ) { backStackEntry ->
                val noticeId = backStackEntry.arguments?.getLong("noticeId")

                if (noticeId != null) {
                    NoticeDetailScreen(
                        noticeId = noticeId,
                        viewModel = HomeViewModel(usersRepository),
                        navController = navController
                    )
                } else {
                    // noticeId가 null인 경우, 기본 동작을 정의합니다.
                    Text("Notice ID is missing")
                }
            }

            //friend
            composable(
                route = "friend/info/{userEmail}",
                arguments = listOf(navArgument("userEmail") { type = NavType.StringType })
            ) { backStackEntry ->
                val userEmail = backStackEntry.arguments?.getString("userEmail")

                if (userEmail != null) {
                    UserInfoScreen(
                        navController = navController,
                        userEmail = userEmail,
                        friendViewModel = FriendViewModel(usersRepository)
                    )
                } else {
                    // noticeId가 null인 경우, 기본 동작을 정의합니다.
                    Text("UserEmail is missing")
                }
            }

            composable(
                route = "friend/notice/{noticeId}",
                arguments = listOf(navArgument("noticeId") { type = NavType.LongType })
            ) { backStackEntry ->
                val noticeId = backStackEntry.arguments?.getLong("noticeId")

                if (noticeId != null) {
                    UserNoticeDetailScreen(
                        noticeId = noticeId,
                        friendViewModel = FriendViewModel(usersRepository),
                        navController = navController
                    )
                } else {
                    // noticeId가 null인 경우, 기본 동작을 정의합니다.
                    Text("Notice ID is missing")
                }
            }

            //setting
            composable("update_user"){
                UpdateUserScreen(navController = navController, viewModel = SettingViewModel(usersRepository))
            }

            composable("setting/first"){
                SettingFirstTabScreen(navController = navController, viewModel = SettingViewModel(usersRepository))
            }
        }



    }
}


@Composable
fun SecurityNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    usersRepository: UsersRepository
){
    val context = LocalContext.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ){
        composable(route = "security"){
            SecurityScreen(navController = navController)
        }

        composable(route = SecurityNav.Login.title){
            LoginScreen(
                navController = navController,
                viewModel = SecurityViewModel(context, usersRepository),
                modifier = Modifier
                    .fillMaxSize()

            )
        }

        composable(route = SecurityNav.Join.title){
            JoinScreen(
                navController = navController,
                //viewModel = SecurityViewModel(),
                viewModel = SecurityViewModel(context, usersRepository),
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }

}

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    usersRepository: UsersRepository
) {
    val context = LocalContext.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        composable(BottomNavItem.Home.screenRoute){
            HomeScreen(navController = navController, homeViewModel = HomeViewModel(usersRepository), componentViewModel = ComponentViewModel())
        }


        composable(BottomNavItem.Friend.screenRoute){
            FriendScreen(navController = navController, friendViewModel = FriendViewModel(usersRepository), componentViewModel = ComponentViewModel())
        }

        composable(BottomNavItem.Setting.screenRoute){
            SettingScreen(navController = navController, settingViewModel = SettingViewModel(usersRepository),componentViewModel = ComponentViewModel())
        }

        //home
        composable("add_notice"){
            AddNoticeScreen(navController = navController, viewModel = HomeViewModel(usersRepository))
        }

        composable(
            route = "home/{noticeId}",
            arguments = listOf(navArgument("noticeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noticeId = backStackEntry.arguments?.getLong("noticeId")

            if (noticeId != null) {
                NoticeDetailScreen(
                    noticeId = noticeId,
                    viewModel = HomeViewModel(usersRepository),
                    navController = navController
                )
            } else {
                // noticeId가 null인 경우, 기본 동작을 정의합니다.
                Text("Notice ID is missing")
            }
        }

        //friend
        composable(
            route = "friend/info/{userEmail}",
            arguments = listOf(navArgument("userEmail") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail")

            if (userEmail != null) {
                UserInfoScreen(
                    navController = navController,
                    userEmail = userEmail,
                    friendViewModel = FriendViewModel(usersRepository)
                )
            } else {
                // noticeId가 null인 경우, 기본 동작을 정의합니다.
                Text("UserEmail is missing")
            }
        }

        composable(
            route = "friend/notice/{noticeId}",
            arguments = listOf(navArgument("noticeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noticeId = backStackEntry.arguments?.getLong("noticeId")

            if (noticeId != null) {
                UserNoticeDetailScreen(
                    noticeId = noticeId,
                    friendViewModel = FriendViewModel(usersRepository),
                    navController = navController
                )
            } else {
                // noticeId가 null인 경우, 기본 동작을 정의합니다.
                Text("Notice ID is missing")
            }
        }

        //setting
        composable("update_user"){
            UpdateUserScreen(navController = navController, viewModel = SettingViewModel(usersRepository))
        }

        composable("setting/first"){
            SettingFirstTabScreen(navController = navController, viewModel = SettingViewModel(usersRepository))
        }
    }
}