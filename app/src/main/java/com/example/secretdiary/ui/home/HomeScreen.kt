package com.example.secretdiary.ui.home

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.secretdiary.R
import com.example.secretdiary.di.model.notice.RNoticeModel
import com.example.secretdiary.ui.components.ComponentViewModel
import com.example.secretdiary.ui.theme.SecretDiaryTheme
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import com.skydoves.landscapist.glide.GlideImage
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale


@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    componentViewModel: ComponentViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showDetailNotice by remember { mutableStateOf(false) }
    var selectedNoticeId by remember { mutableStateOf<Long?>(null) }

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

            Spacer(modifier = Modifier.height(10.dp))

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
                Log.d("home screen", "search test route = ${noticeId}")

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

            Divider(color = Color.Black, thickness = 1.dp)

            // NoticeListItem 클릭 시 showDetailNotice를 true로 변경
            //recycler view
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
                    Log.d("home screen", "RecyclerViewNoticeContent route = ${notice.noticeId}")
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
            }
            .padding(8.dp)
    ) {
        NoticeImage(
            notice = notice,
            modifier = Modifier
                .padding(8.dp)
                .size(84.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Column {
            Text(text = "this Id is ${notice.noticeId}")
            Text(text = notice.noticeTitle)
            Text(text = notice.noticeText)
            //Text(text = notice.date) //test date update
        }
    }
}

@Composable
fun NoticeImage(
    notice: RNoticeModel,
    modifier: Modifier
) {
    val imageUrl = "http://10.0.2.2:8080/notice/image/${notice.noticeImgPath}"

    GlideImage(
        imageModel = imageUrl,
        contentDescription = "Notice Image",
        modifier = modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop,
        error = ImageBitmap.imageResource(id = R.drawable.test)

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

    Log.d("home screen", "RecyclerViewNoticeContent route = ${notice}")

    val scrollState = rememberScrollState() //scroll

    notice?.let { safeNotice ->
        Column(
            modifier = Modifier
                //.padding(4.dp)
                .verticalScroll(scrollState) //scroll
        ) {
            Text(
                text = safeNotice.noticeTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp) // 제목 아래 여백
            )

            Log.d("date check","date : ${safeNotice.date.toString()}")

            Text(
                text = "작성일자 : ${safeNotice.date}", //%{it.noticeDate}
                //text = "작성일자 : ${LocalDateTime.now()}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp) // 작성일자 아래 여백
            )

            Divider(color = Color.Black, thickness = 1.dp)

            NoticeImage(
                notice = safeNotice,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    //.height(300.dp)
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
                    viewModel.addNotice(context)
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

/*
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
}*/