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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.secretdiary.ui.components.ComponentViewModel
import com.example.secretdiary.ui.friend.FriendScreen
import com.example.secretdiary.ui.security.JoinScreen
import com.example.secretdiary.ui.security.SecurityViewModel
import com.example.secretdiary.ui.setting.SettingScreen
import com.example.secretdiary.ui.theme.SecretDiaryTheme
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    componentViewModel: ComponentViewModel,
    modifier: Modifier = Modifier
) {
    var showDetailNotice by remember { mutableStateOf(false) }
    var selectedNoticeId by remember { mutableStateOf<Long?>(null) }

    // searchQuery, searchResults, notices 설정 코드 생략
    var searchQuery by remember { mutableStateOf("")}
    val searchResults by homeViewModel.searchResults.collectAsState()
    val notices by homeViewModel.notices.collectAsState()

    if (showDetailNotice && selectedNoticeId != null) {
        NoticeDetailScreen(
            navController = navController,
            noticeId = selectedNoticeId,
            viewModel = homeViewModel
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                    homeViewModel.onSearchQueryChange(searchQuery)
                },
                label = { Text("Search Notices")}
            )

            val itemsToShow = if (searchQuery.isNotEmpty()) searchResults else notices


            fun onNoticeClick(noticeId: Long) {
                // 예시: 공지사항 세부 정보 화면으로 이동
                navController.navigate("home/$noticeId")
            }


            //search test
            LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp)) {
                items(
                    items = searchResults,
                    //items = itemsToShow,
                    itemContent = { notice ->
                        NoticeListItem(notice = notice, navController = navController) {
                            onNoticeClick(notice.noticeId)
                        }
                    }
                )

            }

            Text(text = "Home2")

            // SearchBar, LazyColumn 등의 기존 Home 화면 요소 생략

            // NoticeListItem 클릭 시 showDetailNotice를 true로 변경
            RecyclerViewNoticeContent(viewModel = homeViewModel, navController = navController) { noticeId ->
                selectedNoticeId = noticeId
                showDetailNotice = true
            }
        }
    }
}



@Composable
fun RecyclerViewNoticeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
    onNoticeClick: (Long) -> Unit
) {
    val notices by viewModel.notices.collectAsState()

    LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp)) {
        items(
            items = notices,
            itemContent = { notice ->
                NoticeListItem(notice = notice, navController = navController) {
                    //onNoticeClick(notice.noticeId)
                    navController.navigate("home/${notice.noticeId}")
                }
            }
        )
    }
}

//검색 결과 및 기본 recycler outer
@Composable
fun NoticeListItem(notice: RNoticeModel, navController: NavHostController, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable {
                onClick()
                //navController.navigate("noticeDetail/$notice.noticeId")
            }
            .padding(8.dp)
    ) {
        NoticeImage(notice = notice)
        Column {
            Text(text = "this is test")
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
    noticeId: Long?,
    viewModel: HomeViewModel
) {
    if (noticeId == null) {
        Text("Error: Notice ID is missing")
        return
    }

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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecretDiaryTheme {
        // Preview에선 ViewModel을 직접 생성해서 전달
        HomeScreen(navController = rememberNavController(),
            homeViewModel = HomeViewModel(),
            componentViewModel = ComponentViewModel()
        )
    }
}