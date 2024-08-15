package com.example.secretdiary.ui.setting

import android.net.Uri
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.secretdiary.R
import com.example.secretdiary.ui.components.ComponentViewModel
import com.example.secretdiary.ui.home.HomeViewModel
import com.example.secretdiary.ui.home.ListItemButton

@Composable
fun SettingScreen(
    navController: NavHostController,
    settingViewModel: SettingViewModel,
    componentViewModel: ComponentViewModel,
    modifier: Modifier = Modifier
) {
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
                    navController.navigate("update_user")
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
fun UpdateUserScreen(
    navController: NavHostController,
    viewModel: SettingViewModel,
    modifier: Modifier = Modifier
){

    //var == 가변
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

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
        Text(text = "Nickname")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("소개 메시지") },
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
                    //viewModel.updateUser(context)
                    navController.popBackStack()
                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("작성하기")
            }
        }

    }
}