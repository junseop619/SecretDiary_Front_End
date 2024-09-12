package com.example.secretdiary.ui.friend

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.secretdiary.R
import com.example.secretdiary.di.model.friend.FriendModel
import com.example.secretdiary.di.model.user.RUserModel
import com.example.secretdiary.di.room.UserDatabase
import com.example.secretdiary.di.room.repository.OfflineUsersRepository
import com.example.secretdiary.di.room.repository.UsersRepository
import com.example.secretdiary.ui.components.ComponentViewModel
import com.example.secretdiary.ui.home.HomeViewModel
import com.example.secretdiary.ui.home.NoticeImage
import com.example.secretdiary.ui.home.NoticeListItem
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FriendScreen(
    navController: NavHostController,
    friendViewModel: FriendViewModel,
    componentViewModel: ComponentViewModel
) {
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
            0 -> TabFriend1(
                friendViewModel = friendViewModel,
                navController = navController
            )
            1 -> TabFriend2(
                friendViewModel = friendViewModel,
                navController = navController
            )
        }

    }
}


//userInfo에 대하여 일단 클릭시 게시물은 나오지 않으나, 친구추가되면 게시물 볼 수 있게 하기
@Composable
fun UserInfoScreen(
    navController: NavHostController,
    userEmail: String?,
    friendViewModel: FriendViewModel
){
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val user by friendViewModel.user.collectAsState()
    val friendCheck by friendViewModel.friendExist.collectAsState()
    val notices by friendViewModel.notices.collectAsState()

    var showDetailNotice by remember { mutableStateOf(false)}
    var selectedNoticeId by remember { mutableStateOf<Long?>(null)}

    friendViewModel.loadUserInfo(userEmail!!)
    //friendViewModel.fetchNotices()
    //후보 1
    Log.d("read user notice22", "success: email : ${userEmail} notices loaded")
    friendViewModel.readFriendNotice(userEmail) //후보2

    /*
    LaunchedEffect(userEmail) {
        friendViewModel.readFriendNotice(userEmail)
    }*/


    //room
    var userRoomEmail by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val userDao = UserDatabase.getDatabase(context).userDao()
        val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

        withContext(Dispatchers.IO) {
            userRoomEmail = usersRepository.getMostRecentUserName()
        }

    }

    if(showDetailNotice && selectedNoticeId != null){
        UserNoticeDetailScreen(
            navController = navController,
            noticeId = selectedNoticeId,
            friendViewModel = friendViewModel
        )
    } else {
        if(userEmail == null){
            Text("Error : User Email is missing")
            return
        } else {
            //Text("OK : OK User Email is : ${userEmail}")
            friendViewModel.loadUserInfo(userEmail)

            if (userRoomEmail != null) {
                Log.d("Load User Room3", "userEmail = $userRoomEmail")
                friendViewModel.checkMyFriend(userRoomEmail!!, userEmail)
            } else {
                Log.d("Load User Room", "Failed")
            }
        }


        Column(modifier = Modifier.fillMaxSize()) {
            when {
                user == null -> {
                    Text("Error : User Email is missing")
                }
                else -> {
                    Log.d("UserInfoScreen", "User loaded: ${user?.userEmail}")
                    Text(text = "Profile")
                    Row {
                        com.example.secretdiary.ui.setting.UserImage(
                            imageUri = user?.userImgPath,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(84.dp)
                                .clip(RoundedCornerShape(CornerSize(16.dp)))
                        )
                        Column {
                            Text(text = user?.userEmail ?: "No Name")
                            Text(
                                text = user?.userText ?: "No Text",
                                modifier = Modifier
                                    .size(width = 80.dp, height = 100.dp)
                                    .padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Divider(color = Color.Black, thickness = 1.dp)

                    //친구라면 notice도 나오게
                    //필요한 것
                    // 1) 친구 검증
                    // 2) 검증이후에 대하여 -> 성공시 notice 나오게, 실패시 친구 요청 버튼
                    if(friendCheck == true){
                        Text("내 친구입니다.")
                        //friendViewModel.readFriendNotice(userEmail) //후보2
                        //notice 나오는 ui
                        RecyclerViewUserNoticeContent(friendViewModel = friendViewModel, navController = navController) { noticeId ->
                            selectedNoticeId = noticeId
                            showDetailNotice = true
                        }
                    } else {
                        //친구 요청 버튼 ui
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                friendViewModel.sendFriendRequest(userRoomEmail!!, userEmail)
                        }) {
                            Text("친구 요청")
                        }


                    }
                }
            }
        }
    }
}

@Composable
fun RecyclerViewUserNoticeContent(
    friendViewModel: FriendViewModel,
    navController: NavHostController,
    onNoticeClick: (Long) -> Unit
){
    val notices by friendViewModel.notices.collectAsState()

    Log.d("Notices Size", "Number of notices: ${notices.size}")
    Text("Number of notices: ${notices.size}")

    LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp)) {
        items(
            items = notices,
            itemContent = { notice ->
                NoticeListItem(notice = notice, navController = navController) {
                    //onNoticeClick(notice.noticeId)
                    navController.navigate("friend/notice/${notice.noticeId}")
                    Log.d("RecyclerViewUserNotice screen", "RecyclerViewNoticeContent route = ${notice.noticeId}")
                }
            }
        )
    }
}

@Composable
fun UserNoticeDetailScreen(
    navController: NavHostController,
    noticeId: Long?,
    friendViewModel: FriendViewModel
){
    if(noticeId == null){
        Text("Error: Notice ID is missing")
        return
    }

    val noticeFlow = friendViewModel.getNoticeById(noticeId)
    val notice by noticeFlow.collectAsState(initial = null)
    Log.d("home screen", "RecyclerViewNoticeContent route = ${notice}")

    val scrollState = rememberScrollState() //scroll

    notice?.let { safeNotice ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            Text(
                text = safeNotice.noticeTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            Text(
                text = "작성일자 : tempo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Divider(color = Color.Black, thickness = 1.dp)

            NoticeImage(
                notice = safeNotice,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
            )

            Divider(color = Color.Black, thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = safeNotice.noticeText,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    } ?: Text("Loading...")
}

@Composable
fun UserImage(
    //user: RUserModel,
    imageUri: String?,
    modifier: Modifier = Modifier
) {
    //val imageUrl = "http://10.0.2.2:8080/user/image/${user.userImgPath}"
    val imageUrl = "http://10.0.2.2:8080/user/image/${imageUri}"

    GlideImage(
        imageModel = imageUrl,
        contentDescription = "User Image",
        modifier = modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop,
        error = ImageBitmap.imageResource(id = R.drawable.test)

    )
}