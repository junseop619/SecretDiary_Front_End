package com.example.secretdiary.ui.setting

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secretdiary.di.SecretDiaryObject
import com.example.secretdiary.di.model.user.RUserModel
import com.example.secretdiary.di.room.repository.UsersRepository
import com.example.secretdiary.ui.security.SecurityViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

import java.io.File
import javax.inject.Inject

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UsersRepository
) : ViewModel(){

    var userNickName : String by mutableStateOf("")
    var userText : String by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)

    /*
    private val _user = MutableStateFlow<RUserModel?>(null)
    val user: StateFlow<RUserModel?> = _user */

    private val _user = MutableStateFlow<RUserModel?>(null)
    val user: StateFlow<RUserModel?> = _user


    fun loadUserInfo(userEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val call = SecretDiaryObject.getRetrofitSDService.userInfo(userEmail)
            call.enqueue(object : Callback<RUserModel> {
                override fun onResponse(call: Call<RUserModel>, response: Response<RUserModel>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _user.value = it
                            Log.d("Load User viewModel", "success")
                            Log.d("Load User viewModel", it.userNickName)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val statusCode = response.code()
                        Log.d("Load User", "Failed: StatusCode = $statusCode, Error = $errorBody")
                    }
                }

                override fun onFailure(call: Call<RUserModel>, t: Throwable) {
                    Log.e("Load User", "Network request failed", t)
                }
            })
        }
    }


    /*
    fun loadUserInfo(userEmail: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val call = SecretDiaryObject.getRetrofitSDService.userInfo(userEmail)
            call.enqueue(object: Callback<RUserModel>{
                override fun onResponse(call: Call<RUserModel>, response: Response<RUserModel>){
                    if(response.isSuccessful){
                        response.body()?.let {
                            _user.value = it

                            Log.d("Load User viewModel", "success")
                            Log.d("Load User viewModel", it.userNickName)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val statusCode = response.code()
                        Log.d("Load User", "Failed: StatusCode = $statusCode, Error = $errorBody")
                    }
                }
                override fun onFailure(call: Call<RUserModel>, t: Throwable){
                    Log.e("Load User", "Network request failed", t)
                }
            })
        }
    }*/

    fun updateUser(context: Context){

        val file = File(context.cacheDir, "upload_image.jpg")
        try {
            context.contentResolver.openInputStream(imageUri!!)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d("Update User", "File created at: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("Update User", "Failed to process image file: ${e.message}")
            return
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val fileBody = MultipartBody.Part.createFormData("userImg", file.name, requestFile)

        viewModelScope.launch(Dispatchers.IO) {
            val userEmail = userRepository.getMostRecentUserName()
            val response = SecretDiaryObject.getRetrofitSDService.updateUser(userEmail!!, userNickName, userText, fileBody)
            if(response.isSuccessful){
                Log.d("Update User","Success")
            } else {
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("Update User", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }

    fun logout(context: Context){
        val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        if(token != null){
            viewModelScope.launch(Dispatchers.IO) {
                val response = SecretDiaryObject.getRetrofitSDService.logout(token)
                if(response.isSuccessful){
                    Log.d("Logout", "서버 로그아웃 성공")

                    //토큰 삭제
                    sharedPreferences.edit().remove("jwt_token").apply()
                    Log.d("Logout", "토큰이 삭제되었습니다.")
                } else {
                    Log.e("Logout", "Logout")
                }
            }
        } else {
            Log.d("Logout", "토큰이 없습니다.")
        }
    }

    fun deleteUser(){
        Log.d("delete","delete")
    }




}