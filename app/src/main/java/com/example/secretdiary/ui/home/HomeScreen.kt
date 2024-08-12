package com.example.secretdiary.ui.home

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.secretdiary.R
import com.example.secretdiary.di.data.DataProvider
import com.example.secretdiary.di.data.Puppy
import com.example.secretdiary.di.notice.model.NoticeModel
import com.example.secretdiary.di.notice.model.RNoticeModel
import com.example.secretdiary.ui.security.JoinScreen
import com.example.secretdiary.ui.security.SecurityViewModel
import com.example.secretdiary.ui.theme.SecretDiaryTheme
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("")}
    val searchResults by viewModel.searchResults.collectAsState()
    val notices by viewModel.notices.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        //verticalArrangement = Arrangement.Center,
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = searchQuery,
            singleLine = true,
            onValueChange = {
                searchQuery = it
                viewModel.onSearchQueryChange(searchQuery)
            },
            label = { Text("Search Notices")}
        )

        val itemsToShow = if (searchQuery.isNotEmpty()) searchResults else notices

        //search test
        LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp)) {
            items(
                items = searchResults,
                //items = itemsToShow,
                itemContent = { notice ->
                    NoticeListItem(notice = notice, navController = navController)
                }
            )

        }

        Text(text = "Home2")
        //RecyclerViewNoticeContent(viewModel = HomeViewModel())
        RecyclerViewNoticeContent(viewModel = viewModel, navController = navController)

    }

}


@Composable
fun RecyclerViewNoticeContent(viewModel: HomeViewModel, navController: NavHostController){
    val notices by viewModel.notices.collectAsState()

    LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp)) {
        items(
            items = notices,
            itemContent = { notice ->
                NoticeListItem(notice = notice, navController = navController)
            }
        )
    }
}

@Composable
fun NoticeListItem(notice: RNoticeModel, navController: NavHostController) {
    Row(
        modifier = Modifier
            .clickable {
                navController.navigate("notice_detail/${notice.noticeId}")
            }
            .padding(8.dp)
    ) {
        NoticeImage(notice = notice)
        Column {
            Text(text = notice.noticeTitle)
            Text(text = notice.noticeText)
        }
    }
}

@Composable
fun NoticeImage(notice: RNoticeModel) {
    Log.d("NoticeImage", "Image URL: ${notice.noticeImgPath}")
    val imageUrl = "http://10.0.2.2:8080/notice/image/${notice.noticeImgPath}"
    val context = LocalContext.current

    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                Glide.with(context)
                    .load(imageUrl)
                    .apply(RequestOptions().placeholder(R.drawable.test).error(R.drawable.test))
                    .into(this)
            }
        },
        modifier = Modifier
            .padding(8.dp)
            .size(84.dp)
            .clip(RoundedCornerShape(16.dp))
    )

}

@Composable
fun NoticeDetailScreen(
    navController: NavHostController,
    noticeId: Long,
    viewModel: HomeViewModel
) {
    val noticeFlow = viewModel.getNoticeById(noticeId)
    val notice by noticeFlow.collectAsState(initial = null)

    notice?.let {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = it.noticeTitle)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it.noticeText)
        }
    } ?: Text("Loading...")
}





@Composable
fun AddNoticeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
){

    //var == 가변
    var selectedImageUri by remember { mutableStateOf<Uri?>(null)}

    val context = LocalContext.current

    //val == 불변(final)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        viewModel.imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "제목")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            label = { Text("제목") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "내용")
        TextField(
            value = viewModel.text,
            onValueChange = { viewModel.text = it },
            label = { Text("내용") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))


        Row {
            Text(text = "사진 첨부")
            Button(onClick = {
                launcher.launch("image/*")
            }) {
                Text("사진첨부")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "제한 조건")

        Spacer(modifier = Modifier.height(16.dp))

        /*
        Image(
            painter = painterResource(id = R.drawable.add_img),
            contentDescription = "addImage",
            modifier = Modifier.size(100.dp)
        )*/

        selectedImageUri?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Selected Image",
                modifier = Modifier.size(100.dp)
            )
        } ?: Image(
            painter = painterResource(id = R.drawable.add_img),
            contentDescription = "addImage",
            modifier = Modifier.size(100.dp)
        )



        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()){
            Button(
                onClick = {
                    //viewModel.addNotice(context)
                    viewModel.addNotice2(context)
                    navController.popBackStack()
                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("작성하기")
            }
        }
        
    }
}


@Composable
fun FriendScreen() {
    //Text(text = "Friend")

    val tabs = listOf("내 친구", "친구 추천")
    val selectedTabIndex = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier.fillMaxSize(),
        //verticalArrangement = Arrangement.Center
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            modifier = Modifier.fillMaxWidth(),

            ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    text = { Text(tab) },
                    selected = selectedTabIndex.value == index,
                    onClick = {
                        coroutineScope.launch {
                            selectedTabIndex.value = index
                        }
                    }
                )
            }
        }
        when (selectedTabIndex.value) {
            0 -> TabFriend1()
            1 -> TabFriend2()
        }

    }
}

@Composable
fun TabFriend1() {
    Column {
        Text(text = "Content for Tab 1")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            //value = searchQuery ?: "",
            value = "",
            singleLine = true,
            onValueChange = {
                val query = it.trim()
                //viewModel.updateSearchQuery(query)

            }
        )
        Text(text = "Content for Tab 1")

    }
}

@Composable
fun TabFriend2() {
    Text(text = "Content for Tab 2")
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        //value = searchQuery ?: "",
        value = "",
        singleLine = true,
        onValueChange = {
            val query = it.trim()
            //viewModel.updateSearchQuery(query)

        }
    )
    Text(text = "Content for Tab 2")
}



@Composable
fun SettingScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),

    ) {
        Text(text = "Profile")
        Row(

        ) {
            Image(
                painter = painterResource(id = R.drawable.test),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .size(84.dp)
                    .clip(RoundedCornerShape(CornerSize(16.dp)))
            )

            Column(

            ) {
                Text(text = "nickName")
                Text(
                    text = "text",
                    modifier = Modifier
                        .size(width = 80.dp, height = 100.dp) //text 영역 size
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
        }
        Spacer(modifier = Modifier.height(1.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    //viewModel.onSignUp()
                    //navController.popBackStack() // 회원가입 후 이전 화면으로 이동
                    //navController.navigate(SecurityNav.Join.name)
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.8f)
            ) {
                Text("내정보 수정하기")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 검정색 긴 실선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black)
        )

        // 리스트 버튼들
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(listOf(
                "리스트 아이템 1" to { Toast.makeText(context, "list 1", Toast.LENGTH_SHORT).show() },
                "리스트 아이템 2" to { Toast.makeText(context, "list 2", Toast.LENGTH_SHORT).show() },
                "리스트 아이템 3" to { Toast.makeText(context, "list 3", Toast.LENGTH_SHORT).show() }
            )) { (item, onClick) ->
                ListItemButton(text = item, onClick = onClick)
                Divider(
                    color = Color.Black,
                    thickness = 1.dp
                )
            }
        }

    }
}

@Composable
fun ListItemButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 0.dp, vertical = 0.dp) // 여백 제거
            //.background(MaterialTheme.colorScheme.surface) // 배경 설정
            .background(color = Color.Green)
            .padding(16.dp) // 내부 패딩 설정
    ) {
        Text(text)
    }
}




sealed class BottomNavItem(
    val title: Int, val icon: Int, val screenRoute: String
) {
    object Home : BottomNavItem(R.string.Home, R.drawable.ic_home, "CALENDAR")
    object Friend : BottomNavItem(R.string.Friend, R.drawable.ic_person, "TIMELINE")
    object Setting : BottomNavItem(R.string.Setting, R.drawable.ic_settings, "ANALYSIS")
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    TopAppBar(
        title = { Text(text = "Secret Diary") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            if(currentRoute == "add_notice"){
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
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = "Home Action"
                        )
                    }
                }
                BottomNavItem.Friend.screenRoute -> {
                    IconButton(onClick = { /* Friend action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "Friend Action"
                        )
                    }
                }
                BottomNavItem.Setting.screenRoute -> {
                    IconButton(onClick = { /* Setting action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Setting Action"
                        )
                    }
                }
            }
        }
    )
}



@Composable
fun MainScreen(viewModel: HomeViewModel) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MainTopAppBar(navController = navController)},
        bottomBar = {
            MyBottomNavigation(
                containerColor = Color.Red,
                contentColor = Color.White,
                indicatorColor = Color.Green,
                navController = navController
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            MyNavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.screenRoute
            )
        }
    }
}

@Composable
private fun MyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        composable(BottomNavItem.Home.screenRoute){
            HomeScreen(navController = navController, viewModel = HomeViewModel())
        }

        composable(BottomNavItem.Friend.screenRoute){
            FriendScreen()
        }

        composable(BottomNavItem.Setting.screenRoute){
            SettingScreen()
        }

        composable("add_notice"){
            AddNoticeScreen(navController = navController, viewModel = HomeViewModel())
        }

        composable(
            route = "notice_detail/{noticeId}",
            //route = "notice_detail",
            arguments = listOf(navArgument("noticeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noticeId = backStackEntry.arguments?.getLong("noticeId")
            noticeId?.let {
                NoticeDetailScreen(noticeId = it, viewModel = HomeViewModel(), navController = navController)
            }
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
        BottomNavItem.Setting
    )

    AnimatedVisibility(
        visible = items.map { it.screenRoute }.contains(currentRoute)
    ) {
        NavigationBar(
            modifier = modifier,
            containerColor = containerColor,
            contentColor = contentColor,
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.screenRoute,
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecretDiaryTheme {
        // Preview에선 ViewModel을 직접 생성해서 전달
        HomeScreen(navController = rememberNavController(),
            viewModel = HomeViewModel()
        )
    }
}