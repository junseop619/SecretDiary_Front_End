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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel(){

    var name : String by mutableStateOf("")
    var text : String by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)

    /*
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
        val fileBody = MultipartBody.Part.createFormData("noticeImg", file.name, requestFile)

        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.upload("userTest",title,text,fileBody)
            if(response.isSuccessful){
                Log.d("Add Notice","Success")
            } else {
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("Add Notice", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }

     */

     */
}