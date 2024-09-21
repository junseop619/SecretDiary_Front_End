package com.example.secretdiary.ui.security

import android.graphics.Paint.Join
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.secretdiary.di.room.UserDatabase
import com.example.secretdiary.di.room.repository.OfflineUsersRepository
import com.example.secretdiary.di.room.repository.UsersRepository
import com.example.secretdiary.ui.components.BottomNavItem
import com.example.secretdiary.ui.components.ComponentViewModel
import com.example.secretdiary.ui.components.MainScreen
import com.example.secretdiary.ui.components.MyNavHost
import com.example.secretdiary.ui.home.HomeViewModel
import com.example.secretdiary.ui.setting.SettingViewModel
//import com.example.secretdiary.ui.home.MainScreen
import com.example.secretdiary.ui.theme.SecretDiaryTheme

enum class SecurityNav(val title: String){
    Login(title = "login"),
    Join(title = "join"),
    Main(title = "main")
}

@Composable
fun SecurityScreen(navController: NavHostController = rememberNavController()) {
    //val navController = rememberNavController()
    val context = LocalContext.current
    val userDao = UserDatabase.getDatabase(context).userDao()
    val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

    //val securityViewModel = remember { SecurityViewModel(context, usersRepository) }



    LaunchedEffect(Unit) {
        //securityViewModel.autoLogin()
        navController.navigate("login2")
    }

    Scaffold() {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)){
            MyNavHost(
                navController = navController,
                startDestination = SecurityNav.Login.title,
                usersRepository = usersRepository
            )
        }
    }
}



@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val requestResult by viewModel.requestResult.collectAsState()
    val alertMessage by remember { viewModel::alertMessage }

    val currentRoute = navController.currentDestination
    Log.d("current route","this route is = ${currentRoute}")

    LaunchedEffect(requestResult) {
        if(requestResult == true){
            navController.navigate(SecurityNav.Main.title)
            viewModel.resetResult()
        } else if(requestResult == false){
            viewModel.resetResult()
        }
        val userDao = UserDatabase.getDatabase(context).userDao()
        val usersRepository: UsersRepository = OfflineUsersRepository(userDao)
        //usersRepository.clearRoom()
    }

    if (alertMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAlert() },
            title = { Text(text = "로그인 에러") },
            text = { Text(text = alertMessage ?: "로그인 실패") },
            confirmButton = {
                Button(onClick = { viewModel.dismissAlert() }) {
                    Text(text = "OK")
                }
            }
        )
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = viewModel.loginEmail,
                onValueChange = { viewModel.loginEmail = it },
                label = { Text("ID") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = viewModel.loginPassword,
                onValueChange = { viewModel.loginPassword = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                viewModel.onLogin()
            }) {
                Text("로그인")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = { viewModel.onFindPassword() }) {
                Text("비밀번호 찾기")
            }
            TextButton(onClick = {
                //viewModel.onSignUp()
                navController.navigate(SecurityNav.Join.title)
            }) {
                Text("회원가입")
            }
        }
    }

}

@Composable
fun LoginScreen2(
    navController: NavHostController,
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val requestResult by viewModel.requestResult.collectAsState()
    val alertMessage by remember { viewModel::alertMessage }

    val currentRoute = navController.currentDestination
    Log.d("current route","this route is = ${currentRoute}")

    LaunchedEffect(requestResult) {
        if(requestResult == true){
            navController.navigate(SecurityNav.Main.title)
            viewModel.resetResult()
        } else if(requestResult == false){
            viewModel.resetResult()
        }
        val userDao = UserDatabase.getDatabase(context).userDao()
        val usersRepository: UsersRepository = OfflineUsersRepository(userDao)
        //usersRepository.clearRoom()
    }

    if (alertMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAlert() },
            title = { Text(text = "로그인 에러") },
            text = { Text(text = alertMessage ?: "로그인 실패") },
            confirmButton = {
                Button(onClick = { viewModel.dismissAlert() }) {
                    Text(text = "OK")
                }
            }
        )
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = viewModel.loginEmail,
                onValueChange = { viewModel.loginEmail = it },
                label = { Text("ID") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = viewModel.loginPassword,
                onValueChange = { viewModel.loginPassword = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                viewModel.onLogin()
            }) {
                Text("로그인")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = { viewModel.onFindPassword() }) {
                Text("비밀번호 찾기")
            }
            TextButton(onClick = {
                //viewModel.onSignUp()
                navController.navigate(SecurityNav.Join.title)
            }) {
                Text("회원가입")
            }
        }
    }

}




@Composable
fun JoinScreen(
    navController: NavHostController,
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "회원가입")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("아이디") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.onSignUp()
            navController.popBackStack() // 회원가입 후 이전 화면으로 이동
            //navController.navigate(SecurityNav.Join.name)
        }) {
            Text("가입하기")
        }
    }
}
