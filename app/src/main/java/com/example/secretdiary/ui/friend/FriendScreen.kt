package com.example.secretdiary.ui.friend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.secretdiary.ui.components.ComponentViewModel
import com.example.secretdiary.ui.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun FriendScreen(viewModel: ComponentViewModel) {
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