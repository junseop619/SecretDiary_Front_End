package com.example.secretdiary.ui.security

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secretdiary.di.SecretDiaryObject
import com.example.secretdiary.di.room.User
import com.example.secretdiary.di.room.UserDatabase
import com.example.secretdiary.di.room.repository.OfflineUsersRepository
import com.example.secretdiary.di.room.repository.UsersRepository
import com.example.secretdiary.di.model.user.LoginModel
import com.example.secretdiary.di.model.user.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UsersRepository
) : ViewModel() {

    //constructor() : this(OfflineUsersRepository(UserDatabase.getDatabase().userDao()))

    var id: String by mutableStateOf("")
    var email: String by mutableStateOf("")
    var password: String by mutableStateOf("")
    var name: String by mutableStateOf("")

    var loginEmail: String by mutableStateOf("")
    var loginPassword: String by mutableStateOf("")

    private val result = MutableStateFlow<Boolean?>(null)
    val requestResult : StateFlow<Boolean?> = result

    var alertMessage: String? by mutableStateOf(null)

    init {
        //autoLogin()
    }

    fun resetResult(){
        result.value = null
    }

    fun dismissAlert() {
        alertMessage = null
    }

    fun onLogin() {
        // Handle login logic


        var roomEmail : String?
        //val userDao = UserDatabase.getDatabase(context).userDao()
        //val usersRepository : UsersRepository = OfflineUsersRepository(userDao)

        val model = LoginModel(loginEmail, loginPassword)
        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.loginUser(model)
            if(response.isSuccessful){
                Log.d("Login","Login Success")
                Log.d("null test", email + password)

                //token
                //val token = response.headers()["Authorization"]
                val token = response.body()
                Log.d("auto login in login", "Token saved: $token")

                if(token != null){
                    saveToken(token)
                }

                result.value = true

                //room
                val user = User(userName = loginEmail, autoLogin = true, lastLoginTime = Date())
                userRepository.insertUser(user)
                roomEmail = userRepository.getMostRecentUserName()
                Log.d("recent user", "userEmail = $roomEmail")


            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("Login Error", "Failed to send login dat a. Error response: $errorBody")
                result.value = false

            }
        }

    }

    //sharedPreferences에 token 저장
    private fun saveToken(token: String){
        val sharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("jwt_token", token).apply()
        Log.d("auto login saveToken", "Token saved: $token")
    }

    //validate token
    private fun validationToken(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.autoLogin(token)
            if(response.isSuccessful){
                Log.d("auto login", "success")
                result.value = true
            } else {
                Log.e("auto login", "Token validation failed, prompting user to login.")
                result.value = false
            }
        }
    }

    //auto login
    fun autoLogin(){
        val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        Log.d("autoLogin", "Token retrieved: $token")

        if(token != null){
            Log.d("auto login","token is exist")
            validationToken(token)
        } else {
            Log.d("auto login","token is null")
        }
    }

    fun onFindPassword() {
        // Handle find password logic
    }

    fun onSignUp() {
        // Handle sign up logic
        val model = UserModel(id, name, email, password)
        GlobalScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.joinUser(model)
            if(response.isSuccessful){
                Log.d("Join","Join Success")
            } else {
                Log.e("Join","Failed Join")
            }
        }
    }
}