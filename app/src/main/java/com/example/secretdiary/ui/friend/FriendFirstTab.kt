package com.example.secretdiary.ui.friend

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

//내 친구 tab
//내 친구 목록에 대하여 검색할 수 있게
//내 현재 친구 목록나오게
@Composable
fun TabFriend1(
    friendViewModel: FriendViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showDetailMyFriend by remember { mutableStateOf(false) }
    var selectedMyFriendEmail by remember { mutableStateOf<String?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    val searchResults by friendViewModel.searchResults.collectAsState()
    val myFriends by friendViewModel.myFriends.collectAsState()

    /*
    LaunchedEffect(Unit) {
        friendViewModel.readMyFriendList(context)
    }*/

    friendViewModel.readMyFriendList(context)


    if(showDetailMyFriend && selectedMyFriendEmail != null){
        UserInfoScreen(
            navController = navController,
            userEmail = selectedMyFriendEmail,
            friendViewModel = friendViewModel
        )
    }

    Column {
        Text(text = "내 친구 검색")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = searchQuery,
            singleLine = true,
            onValueChange = {
                searchQuery = it.trim()
                friendViewModel.onSearchQueryChange(searchQuery)
            },
            label = { Text("Search My Friend") }
        )

        fun onFriendClick(){

        }


        LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp)) {
            items(
                items = searchResults,
                itemContent = { myFriend ->
                    FriendListItem(friend = myFriend, navController = navController) {
                        onFriendClick()
                    }
                }
            )
        }

        Divider(color = Color.Black, thickness = 1.dp)

        Text(text = "내 친구 목록")


        RecyclerViewMyFriendContent(viewModel = friendViewModel, navController = navController) { friendEmail ->
            selectedMyFriendEmail = friendEmail
            showDetailMyFriend = true

        }
    }
}

@Composable
fun FriendListItem(friend: FriendModel, navController: NavHostController, onClick: () -> Unit){
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(8.dp)
    ) {
        Text(text = "this friendEmail is ${friend.friendEmail}")
    }
}

//내친구 목록
@Composable
fun RecyclerViewMyFriendContent(
    viewModel: FriendViewModel,
    navController: NavHostController,
    onFriendClick: (String) -> Unit
){
    val myFriends by viewModel.myFriends.collectAsState()

    LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp)) {
        items(
            items = myFriends,
            itemContent = { myFriend ->
                FriendListItem(friend = myFriend, navController = navController) {
                    navController.navigate("friend/info/${myFriend.friendEmail}") //내 친구 정보 보기
                    Log.d("home screen", "RecyclerViewNoticeContent route = ${myFriend.friendEmail}")

                }
            }
        )
    }
}