package com.example.secretdiary.ui.friend

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.secretdiary.di.model.friend.FriendModel
import com.example.secretdiary.di.model.user.RUserModel

//친구 추천 tab
//일반 유저 nickName기반 검색기능 (v)
//검색 후 결과에 대하여 클릭 시 userInfo가 나오게(만약 친구라면 notice포함)
//내가 받은 친구 요청 목록 보기
@Composable
fun TabFriend2(
    friendViewModel: FriendViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery2 by remember { mutableStateOf("") }
    val searchResults2 by friendViewModel.searchResults2.collectAsState()

    val users by friendViewModel.users.collectAsState()

    var showDetailUser by remember { mutableStateOf(false) }
    var selectedUserEmail by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        friendViewModel.readFriendRequest(context)
    }

    //friendViewModel.readFriendRequest(context)

    if(showDetailUser && selectedUserEmail != null){
        UserInfoScreen(
            navController = navController,
            userEmail = selectedUserEmail,
            friendViewModel = friendViewModel
        )
    } else {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "유저 검색")
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = searchQuery2,
                singleLine = true,
                onValueChange = {
                    searchQuery2 = it
                    friendViewModel.onSearchQueryChange2(searchQuery2)
                },
                label = { Text("Search User") }
            )

            val itemsToShow = if (searchQuery2.isNotEmpty()) searchResults2 else users

            fun onUserClick(userEmail: String){
                navController.navigate("friend/info/$userEmail")
                Log.d("home screen", "search test route = ${userEmail}")
            }

            //search user result
            LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp)) {
                items(
                    items = searchResults2,
                    //items = itemsToShow,
                    itemContent = { user ->
                        UserListItem(user = user, navController = navController) {
                            onUserClick(user.userEmail)
                        }
                    }
                )

            }

            Text(text = "친구 요청 목록")
            Divider(color = Color.Black, thickness = 1.dp)

            RecyclerViewFriendRequestContent(viewModel = friendViewModel, navController = navController, context = context){ userEmail ->
                selectedUserEmail = userEmail
                showDetailUser = true
            }
        }
    }
}

@Composable
fun UserListItem(user: RUserModel, navController: NavHostController, onClick: () -> Unit){
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(8.dp)
    ) {
        Text(text = "this userEmail is ${user.userEmail}")
    }
}

@Composable
fun FriendRequestListItem(
    friend: FriendModel,
    frfendViewModel: FriendViewModel,
    navController: NavHostController,
    context: Context,
    onClick: () -> Unit
){

    Row (
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(8.dp)
    ){
        Text(text = "${friend.userEmail} 님이 친구 요청을 보내셨습니다.")
        Button(onClick = {
            frfendViewModel.acceptRequest(friend.friendEmail,friend.userEmail, context)
            //FriendViewModel().refreshFriendRequests(context)
            frfendViewModel.readFriendRequest(context)
        }) {
            Text("수락")
        }

    }
}

//받은 친구 요청 목록 보기
@Composable
fun RecyclerViewFriendRequestContent(
    viewModel: FriendViewModel,
    navController: NavHostController,
    context: Context,
    onUserClick: (String) -> Unit
) {
    val requests by viewModel.requestLists.collectAsState()

    LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp)) {
        items(
            items = requests,
            itemContent = { request ->
                FriendRequestListItem(friend = request, navController = navController, frfendViewModel = viewModel, context = context) {
                    navController.navigate("friend/info/${request.userEmail}") //요청자 정보 보기
                    Log.d("friend screen", "RecyclerViewNoticeContent route = ${request.userEmail}")
                }
            }
        )
    }
}