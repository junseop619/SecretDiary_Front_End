package com.example.secretdiary.ui.setting

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.secretdiary.BuildConfig
import com.example.secretdiary.R
import com.example.secretdiary.di.room.UserDatabase
import com.example.secretdiary.di.room.repository.OfflineUsersRepository
import com.example.secretdiary.di.room.repository.UsersRepository
import com.example.secretdiary.ui.components.ComponentViewModel
import com.example.secretdiary.ui.home.ListItemButton
import com.example.secretdiary.ui.security.SecurityScreen
import com.example.secretdiary.ui.security.SecurityViewModel
import com.example.secretdiary.ui.theme.darkBlue
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SettingScreen(
    navController: NavHostController,
    settingViewModel: SettingViewModel,
    componentViewModel: ComponentViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by settingViewModel.user.collectAsState()

    var userRoomEmail by remember { mutableStateOf<String?>(null) }
    Log.d("Load User Room1", "userEmail = $userRoomEmail")


    var showVersionDialog by remember { mutableStateOf(false) } // 버전 정보
    var showLogOutDialog by remember { mutableStateOf(false)} //로그아웃
    var showDeleteUserDialog by remember { mutableStateOf(false)} //회원 탈퇴


    // LaunchedEffect에서 userRoomEmail에 따라 동작하도록 설정
    LaunchedEffect(Unit) {
        val userDao = UserDatabase.getDatabase(context).userDao()
        val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

        withContext(Dispatchers.IO) {
            userRoomEmail = usersRepository.getMostRecentUserName()
        }

    }

    Log.d("Load User Room2", "userEmail = $userRoomEmail")

    if (userRoomEmail != null) {
        Log.d("Load User Room3", "userEmail = $userRoomEmail")
        settingViewModel.loadUserInfo(userRoomEmail!!)
    } else {
        //Toast.makeText(context, "최근 로그인한 사용자가 없습니다.", Toast.LENGTH_SHORT).show()
        Log.d("Load User Room", "Failed")
    }


    Log.d("Load User Room4", "userEmail = $userRoomEmail")
    // user 상태를 적절히 반영
    Column(modifier = Modifier.fillMaxSize()) {
        when {
            user == null -> {
                Log.d("SettingScreen", "Loading...")
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            else -> {
                Log.d("SettingScreen", "User loaded: ${user?.userEmail}")

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    UserImage(
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
            }
        }
        //---
        Spacer(modifier = Modifier.height(1.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    navController.navigate("update_user")
                },
                colors = ButtonColors(
                    containerColor = darkBlue,
                    contentColor = Color.White,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.Black
                ),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.8f)
            ) {
                Text("내정보 수정하기")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black)
        )

        Text("설정")

        Divider(color = Color.Black, thickness = 1.dp)

        LazyColumn {
            items(listOf(
                "리스트 아이템 1" to {
                    Toast.makeText(context, "list 1", Toast.LENGTH_SHORT).show()
                    navController.navigate("setting/first")
                },
                "버전 정보" to {
                    showVersionDialog = true // 버전 정보 다이얼로그 열기
                },
                "로그아웃" to {
                    showLogOutDialog = true
                },
                "회원 탈퇴" to {
                    showDeleteUserDialog = true
                }
            )) { (item, onClick) ->
                ListItemButton(
                    text = item,
                    onClick = onClick,
                    modifier = Modifier
                )

                Divider(color = Color.Black, thickness = 1.dp)
            }
        }
    }


    if(showVersionDialog){
        VersionInfoDialog(onDismiss = { showVersionDialog = false })
    }

    if(showLogOutDialog){
        LogOutDialog(settingViewModel,onDismiss = {showLogOutDialog = false}, navController = navController)
    }

    if(showDeleteUserDialog){
        DeleteUserDialog(settingViewModel,onDismiss = {showDeleteUserDialog = false}, navController = navController)
    }
}

@Composable
fun ListItemButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier, // modifier 파라미터 추가
    backGroundColor: Color = Color.Transparent, //투명한 배경색
    contentColor: Color = Color.Black
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backGroundColor,
            contentColor = contentColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp),
        shape = RectangleShape
    ) {
        Text(text)
    }
}

@Composable
fun UpdateUserScreen(
    navController: NavHostController,
    viewModel: SettingViewModel,
    modifier: Modifier = Modifier
){


    //var == 가변
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    var userRoomEmail by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        val userDao = UserDatabase.getDatabase(context).userDao()
        val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

        withContext(Dispatchers.IO) {
            userRoomEmail = usersRepository.getMostRecentUserName()
        }
    }
    if (userRoomEmail != null) {
        Log.d("Load User Room3", "userEmail = $userRoomEmail")
        viewModel.loadUserInfo(userRoomEmail!!)
    } else {
        //Toast.makeText(context, "최근 로그인한 사용자가 없습니다.", Toast.LENGTH_SHORT).show()
        Log.d("Load User Room", "Failed")
    }

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
        Text(text = "Nickname")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.userNickName,
            onValueChange = { viewModel.userNickName = it },
            label = { Text("userNickName") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "소개말")
        TextField(
            //value = user?.userText ?: viewModel.userText,
            value = viewModel.userText,
            //value = "ssssss",
            onValueChange = { viewModel.userText = it },
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
                    viewModel.updateUser(context)
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

@Composable
fun SettingFirstTabScreen(
    navController: NavHostController,
    viewModel: SettingViewModel,
    modifier: Modifier = Modifier
){
    var count by remember { mutableStateOf(0) }

    // UI 구성
    Column {
        // 카운트 숫자를 표시하는 텍스트
        Text(text = "Count: $count", fontSize = 30.sp)

        // 버튼을 클릭할 때마다 count를 1씩 증가시킴
        Button(onClick = { count++ }) {
            Text(text = "Increase Count")
        }
    }
}


//버전정보
@Composable
fun VersionInfoDialog(onDismiss: () -> Unit) {
    var version = BuildConfig.VERSION_NAME

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .size(300.dp, 200.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "버전 정보", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "현재 버전: ${version}", fontSize = 16.sp)

                Button(onClick = onDismiss) {
                    Text("닫기")
                }
            }
        }
    }
}


//로그아웃
@Composable
fun LogOutDialog(
    settingViewModel: SettingViewModel,
    navController: NavHostController,
    onDismiss: () -> Unit
) {
    var version = BuildConfig.VERSION_NAME
    val context = LocalContext.current

    val userDao = UserDatabase.getDatabase(context).userDao()
    val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .size(300.dp, 200.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "로그아웃", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "로그아웃 하시겠습니까?", fontSize = 16.sp)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Button(
                        onClick = {
                            settingViewModel.logout(context)
                            onDismiss()
                            SecurityViewModel(context,usersRepository).resetResult()

                            navController.navigate("securityNav") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }




                        }
                    ) {
                        Text("   예   ")
                    }

                    Button(onClick = onDismiss) {
                        Text("아니요")
                    }
                }

            }
        }
    }
}

//회원탈퇴
@Composable
fun DeleteUserDialog(
    settingViewModel: SettingViewModel,
    navController: NavHostController,
    onDismiss: () -> Unit
) {
    var version = BuildConfig.VERSION_NAME

    val context = LocalContext.current

    var userRoomEmail by remember { mutableStateOf<String?>(null) }

    val userDao = UserDatabase.getDatabase(context).userDao()
    val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            userRoomEmail = usersRepository.getMostRecentUserName()
        }
    }


    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .size(300.dp, 200.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "회원탈퇴", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "회원탈퇴 하시겠습니까?", fontSize = 16.sp)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Button(
                        onClick = {
                            settingViewModel.logout(context)
                            Log.d("delete","email = ${userRoomEmail}")
                            settingViewModel.deleteUser(context, userRoomEmail!!)
                            onDismiss()
                            SecurityViewModel(context,usersRepository).resetResult()
                            navController.navigate("securityNav") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Text("   예   ")
                    }

                    Button(onClick = onDismiss) {
                        Text("아니요")
                    }
                }
            }
        }
    }
}


