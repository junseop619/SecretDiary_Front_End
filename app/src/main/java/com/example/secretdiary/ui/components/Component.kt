package com.example.secretdiary.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.secretdiary.R
import com.example.secretdiary.di.room.UserDatabase
import com.example.secretdiary.di.room.repository.OfflineUsersRepository
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
import com.example.secretdiary.ui.security.LoginScreen2
import com.example.secretdiary.ui.security.SecurityNav
import com.example.secretdiary.ui.security.SecurityScreen
import com.example.secretdiary.ui.security.SecurityViewModel
import com.example.secretdiary.ui.setting.SettingFirstTabScreen
import com.example.secretdiary.ui.setting.SettingScreen
import com.example.secretdiary.ui.setting.SettingViewModel
import com.example.secretdiary.ui.setting.UpdateUserScreen
import com.example.secretdiary.ui.theme.Purple40
import com.example.secretdiary.ui.theme.lightBlue
import com.example.secretdiary.ui.theme.mediumBlue


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
                BottomNavItem.Friend.screenRoute -> {
                    /*
                    IconButton(onClick = { /* Friend action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "Friend Action"
                        )
                    }*/
                }
                BottomNavItem.Setting.screenRoute -> {
                    /*
                    IconButton(onClick = { /* Setting action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Setting Action"
                        )
                    }*/
                }
            }
        }
    )
}



@Composable
fun MainScreen(
    //navController: NavHostController,
    viewModel: ComponentViewModel
) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val userDao = UserDatabase.getDatabase(context).userDao()
    val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            if(currentRoute != "login2"){
                MainTopAppBar(navController = navController)
            }
            //MainTopAppBar(navController = navController)
                 },
        bottomBar = {
            MyBottomNavigation(
                containerColor = mediumBlue,//Color.Red,
                contentColor = Color.White,
                indicatorColor = lightBlue,
                navController = navController
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            MyNavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.screenRoute,
                usersRepository = usersRepository
            )
        }
        /*
        Box(modifier = Modifier.padding(it)) {
            MyNavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.screenRoute,
                usersRepository = usersRepository
            )
        }*/
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
        //before login
        composable(route = "security"){
            SecurityScreen()
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
        composable(route = SecurityNav.Main.title){
            MainScreen(
                //viewModel = HomeViewModel()
                //navController = navController,
                viewModel = ComponentViewModel()
            )
        }


        composable(route = SecurityNav.Login.title){
            LoginScreen(
                navController = navController,
                //viewModel = SecurityViewModel(),
                viewModel = SecurityViewModel(context, usersRepository),
                modifier = Modifier
                    .fillMaxSize()

            )
        }

        composable(route = "login2"){
            LoginScreen2(
                navController = navController,
                //viewModel = SecurityViewModel(),
                viewModel = SecurityViewModel(context, usersRepository),
                modifier = Modifier
                    .fillMaxSize()

            )
        }


        //after login
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



@Composable
private fun MyBottomNavigation(
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    indicatorColor: Color,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Friend,
        BottomNavItem.Setting,
    )

    AnimatedVisibility(
        //visible = items.map { it.screenRoute }.contains(currentRoute)
        visible = items.any { item ->
            currentRoute?.startsWith(item.screenRoute) == true
        }
    ) {
        NavigationBar(
            modifier = modifier,
            containerColor = containerColor,
            contentColor = contentColor,
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute?.startsWith(item.screenRoute) == true,
                    //selected = currentRoute == item.screenRoute,
                    label = {
                        Text(
                            text = stringResource(id = item.title),
                            style = TextStyle(
                                fontSize = 12.sp
                            )
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = stringResource(id = item.title)
                        )
                    },
                    onClick = {
                        navController.navigate(item.screenRoute) {
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}