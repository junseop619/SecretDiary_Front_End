package com.example.secretdiary.ui.home

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secretdiary.di.SecretDiaryObject
import com.example.secretdiary.di.model.notice.RNoticeModel
import com.example.secretdiary.di.room.UserDatabase
import com.example.secretdiary.di.room.repository.OfflineUsersRepository
import com.example.secretdiary.di.room.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
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
class HomeViewModel @Inject constructor(
    private val userRepository: UsersRepository
) : ViewModel(){

    //add Notice
    var noticeId: Long by mutableLongStateOf(1L)
    var userEmail: String by mutableStateOf("")
    var title: String by mutableStateOf("")
    var text : String by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)

    //read All Notice
    private val _notices = MutableStateFlow<List<RNoticeModel>>(emptyList())
    val notices: StateFlow<List<RNoticeModel>> = _notices

    //search Notice
    private val _searchResults = MutableStateFlow<List<RNoticeModel>>(emptyList())
    val searchResults: StateFlow<List<RNoticeModel>> = _searchResults
    private val searchQuery = MutableStateFlow("")

    // 추가된 로딩 상태
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        readMyNotice()

        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .collectLatest { query ->
                    if(query.isNotEmpty()) {
                        searchNotices(query)
                    } else {
                        _searchResults.value = emptyList()
                    }
                }
        }
    }


    //searchNotice
    fun onSearchQueryChange(keyword: String){
        searchQuery.value = keyword
    }

    fun searchNotices(keyword: String){
        viewModelScope.launch {
            val call = SecretDiaryObject.getRetrofitSDService.search2(keyword)
            call.enqueue(object: Callback<List<RNoticeModel>>{
                override fun onResponse(call: Call<List<RNoticeModel>>, response: Response<List<RNoticeModel>>) {
                    if(response.isSuccessful){
                        response.body()?.let {
                            _searchResults.value = it
                            Log.d("search_read", "success")
                        } ?: Log.d("search_read", "Response body is null")

                    } else {
                        Log.d("search_read", "Response is not successful. Status code: ${response.code()}, Message: ${response.message()}")
                        response.errorBody()?.let {
                            Log.d("search_read", "Error body: ${it.string()}")
                        }
                    }
                }
                override fun onFailure(call: Call<List<RNoticeModel>>, t: Throwable){
                    Log.e("search_read", "Network request failed", t)
                }
            })
        }
    }

    fun addNotice(context: Context){
        val file = File(context.cacheDir, "upload_image.jpg")
        try {
            context.contentResolver.openInputStream(imageUri!!)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d("Add Notice", "File created at: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("Add Notice", "Failed to process image file: ${e.message}")
            return
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val fileBody = MultipartBody.Part.createFormData("noticeImg", file.name, requestFile)

        viewModelScope.launch(Dispatchers.IO) {
            val userDao = UserDatabase.getDatabase(context).userDao()
            val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

            val userEmail = usersRepository.getMostRecentUserName()

            val response = SecretDiaryObject.getRetrofitSDService.upload(userEmail!!,title,text,fileBody)
            if(response.isSuccessful){
                Log.d("Add Notice","Success")
            } else {
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("Add Notice", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }


    fun getNoticeById(noticeId: Long): Flow<RNoticeModel?> {
        return notices.map { list ->
            list.find { it.noticeId == noticeId}
        }
    }

    fun readMyNotice(){
        viewModelScope.launch(Dispatchers.IO) {

            val userEmail = userRepository.getMostRecentUserName()

            val call = SecretDiaryObject.getRetrofitSDService.readUserNotice(userEmail!!)
            call.enqueue(object: Callback<List<RNoticeModel>>{
                override fun onResponse(call: Call<List<RNoticeModel>>, response: Response<List<RNoticeModel>>) {
                    if(response.isSuccessful){
                        response.body()?.let {
                            _notices.value = it
                            Log.d("read user notice", "success")
                            Log.d("date check", "viweModelDate = ${_notices.value}")
                            Log.d("date check Raw Response", response.raw().toString())
                        } ?: Log.d("read user notice", "Response body is null")


                    } else {
                        Log.d("read user notice", "Response is not successful. Status code: ${response.code()}, Message: ${response.message()}")
                        response.errorBody()?.let {
                            Log.d("read user notice", "Error body: ${it.string()}")
                        }
                    }
                }
                override fun onFailure(call: Call<List<RNoticeModel>>, t: Throwable){
                    Log.e("read user notice", "Network request failed", t)
                }
            })
        }
    }


}