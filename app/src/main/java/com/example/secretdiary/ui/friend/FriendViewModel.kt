package com.example.secretdiary.ui.friend

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secretdiary.di.SecretDiaryObject
import com.example.secretdiary.di.model.friend.FriendModel
import com.example.secretdiary.di.model.notice.RNoticeModel
import com.example.secretdiary.di.model.user.RUserModel
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val userRepository: UsersRepository
) : ViewModel(){

    //my Friend(tab 1)
    private val _myFriends = MutableStateFlow<List<FriendModel>>(emptyList())
    val myFriends: StateFlow<List<FriendModel>> = _myFriends

    //tab 1
    private val _searchResults = MutableStateFlow<List<FriendModel>>(emptyList())
    val searchResults: StateFlow<List<FriendModel>> = _searchResults
    private val searchQuery = MutableStateFlow("")

    //tab 2
    private val _searchResults2 = MutableStateFlow<List<RUserModel>>(emptyList())
    val searchResults2: StateFlow<List<RUserModel>> = _searchResults2
    private val searchQuery2 = MutableStateFlow("")

    private val _users = MutableStateFlow<List<RUserModel>>(emptyList())
    val users: StateFlow<List<RUserModel>> = _users

    private val _user = MutableStateFlow<RUserModel?>(null)
    val user: StateFlow<RUserModel?> = _user

    private val _friendExist = MutableStateFlow<Boolean?>(null)
    val friendExist: StateFlow<Boolean?> = _friendExist

    private val _requestExist = MutableStateFlow<Boolean?>(null)
    val requestExist: StateFlow<Boolean?> = _requestExist

    private val _notices = MutableStateFlow<List<RNoticeModel>>(emptyList())
    val notices: StateFlow<List<RNoticeModel>> = _notices

    private val _requestLists = MutableStateFlow<List<FriendModel>>(emptyList())
    val requestLists: StateFlow<List<FriendModel>> = _requestLists

    private val _detailNotice = MutableStateFlow<RNoticeModel?>(null)
    val detailNotice: StateFlow<RNoticeModel?> = _detailNotice


    init {
        //fetchNotices()
        //readFriendNotice()
        /*
        if(_user.value!!.userEmail != null){
            readFriendNotice(_user.value!!.userEmail)
        }*/

        viewModelScope.launch {
            /*
            searchQuery
                .debounce(300)
                .collectLatest { query ->
                    if(query.isNotEmpty()) {
                        //searchUser(query)
                    } else {
                        _searchResults2.value = emptyList()
                    }
                }*/

            searchQuery2
                .debounce(300)
                .collectLatest { query ->
                    if(query.isNotEmpty()) {
                        searchUser(query)
                    } else {
                        _searchResults2.value = emptyList()
                    }
                }
        }
    }


    fun onSearchQueryChange(keyword: String){
        searchQuery.value = keyword
    }

    fun onSearchQueryChange2(keyword: String){
        searchQuery2.value = keyword
    }


    //tab2 - 유저 검색
    fun searchUser(keyword: String){
        viewModelScope.launch(Dispatchers.IO) {
            val userEmail = userRepository.getMostRecentUserName()
            val response = SecretDiaryObject.getRetrofitSDService.searchUser(keyword, userEmail!!)
            if(response.isSuccessful){
                response.body()?.let {
                    _searchResults2.value = it
                }
            }else{
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("Search_read", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }

    //tab2 - 유저 정보 보기
    fun getUserByEmail(userEmail: String): Flow<RUserModel?>{
        return users.map { list ->
            list.find { it.userEmail == userEmail}
        }
    }

    fun getNoticeById(noticeId: Long): Flow<RNoticeModel?> {
        return notices.map { list ->
            list.find { it.noticeId == noticeId}
        }
    }


    fun loadUserInfo(userEmail: String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.userInfo(userEmail)
            if(response.isSuccessful){
                if (response.isSuccessful) {
                    response.body()?.let {
                        _user.value = it
                    }
                }
            }else{
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("Load User", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }

    //친구 검증
    fun checkMyFriend(userEmail: String, friendEmail: String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.checkFriend(userEmail, friendEmail)
            if(response.isSuccessful){
                response.body()?.let {
                    _friendExist.value = it
                }
            }else{
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("check friend", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }

    //친구요청 검증
    /*
    fun checkRequest(userEmail: String, friendEmail: String){
        viewModelScope.launch(Dispatchers.IO) {
            val call = SecretDiaryObject.getRetrofitSDService.checkRequest(userEmail, friendEmail)
            call.enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _requestExist.value = it
                            Log.d("check request", "success")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val statusCode = response.code()
                        Log.d("check request", "Failed: StatusCode = $statusCode, Error = $errorBody")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.e("check friend", "Network request failed", t)
                }
            })
        }
    }*/

    fun checkRequest(userEmail: String, friendEmail: String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.checkRequest(userEmail, friendEmail)
            if (response.isSuccessful){
                response.body()?.let {
                    _requestExist.value = it
                    Log.d("check request", "success")
                }
            }else{
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("check request", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }



    //친구 요청기능
    fun sendFriendRequest(userEmail: String, friendEmail: String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.sendRequestFriend(userEmail, friendEmail)
            if (response.isSuccessful){
                Log.d("send Friend request","success")
            } else {
                Log.e("send Friend request", "failed")
            }
        }
    }

    //친구 요청 목록 보기
    /*
    fun readFriendRequest(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            val userDao = UserDatabase.getDatabase(context).userDao()
            val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

            val userEmail = usersRepository.getMostRecentUserName()

            val call = SecretDiaryObject.getRetrofitSDService.friendRequestList(userEmail!!)
            call.enqueue(object: Callback<List<FriendModel>>{
                override fun onResponse(call: Call<List<FriendModel>>, response: Response<List<FriendModel>>) {
                    if(response.isSuccessful){
                        response.body()?.let {
                            _requestLists.value = it
                            Log.d("read", "success")
                        } ?: Log.d("read", "Response body is null")


                    } else {
                        Log.d("read", "Response is not successful. Status code: ${response.code()}, Message: ${response.message()}")
                        response.errorBody()?.let {
                            Log.d("read", "Error body: ${it.string()}")
                        }
                    }
                }
                override fun onFailure(call: Call<List<FriendModel>>, t: Throwable){
                    Log.e("read", "Network request failed", t)
                }
            })
        }
    }*/

    fun readFriendRequest(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            val userDao = UserDatabase.getDatabase(context).userDao()
            val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

            val userEmail = usersRepository.getMostRecentUserName()
            val response = SecretDiaryObject.getRetrofitSDService.friendRequestList(userEmail!!)
            if(response.isSuccessful){
                response.body()?.let {
                    _requestLists.value = it
                }
            }else{
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("read friend request", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }

    //요청 수락 받기
    fun acceptRequest(userEmail: String, friendEmail: String, context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.acceptFriendRequest(userEmail, friendEmail)
            if (response.isSuccessful){
                Log.d("accept Friend request","success")

                refreshFriendRequests(context)

                //이후 친구 요청 목록 삭제 시켜야 함

            } else {
                Log.e("accept Friend request", "failed")
            }
        }
    }



    //내 친구 목록 보기
    fun readMyFriendList(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            val userDao = UserDatabase.getDatabase(context).userDao()
            val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

            val userEmail = usersRepository.getMostRecentUserName()
            val response = SecretDiaryObject.getRetrofitSDService.getReadMyFriendList(userEmail!!)
            if(response.isSuccessful){
                response.body()?.let {
                    _myFriends.value = it
                }
            }else{
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("readMyFirendList", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }


    fun readFriendNotice(userEmail: String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.readUserNotice(userEmail)
            if(response.isSuccessful){
                response.body()?.let {
                    _notices.value = it
                }
            } else {
                response.errorBody()?.let {
                    Log.d("read user notice", "Error body: ${it.string()}")
                }
            }
        }
    }


    /*
    fun refreshFriendRequests(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val userDao = UserDatabase.getDatabase(context).userDao()
            val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

            val userEmail = usersRepository.getMostRecentUserName()

            val call = SecretDiaryObject.getRetrofitSDService.friendRequestList(userEmail!!)
            call.enqueue(object: Callback<List<FriendModel>> {
                override fun onResponse(call: Call<List<FriendModel>>, response: Response<List<FriendModel>>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _requestLists.value = it // 친구 요청 목록 갱신
                            Log.d("refresh Friend Request", "success")
                        } ?: Log.d("refresh Friend Request", "Response body is null")
                    } else {
                        Log.d("refresh Friend Request", "Response is not successful. Status code: ${response.code()}, Message: ${response.message()}")
                        response.errorBody()?.let {
                            Log.d("refresh Friend Request", "Error body: ${it.string()}")
                        }
                    }
                }

                override fun onFailure(call: Call<List<FriendModel>>, t: Throwable) {
                    Log.e("refresh Friend Request", "Network request failed", t)
                }
            })
        }
    }*/

    fun refreshFriendRequests(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val userDao = UserDatabase.getDatabase(context).userDao()
            val usersRepository: UsersRepository = OfflineUsersRepository(userDao)

            val userEmail = usersRepository.getMostRecentUserName()
            val response = SecretDiaryObject.getRetrofitSDService.friendRequestList(userEmail!!)
            if(response.isSuccessful){
                response.body()?.let {
                    _requestLists.value = it // 친구 요청 목록 갱신
                }
            }else{
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("refresh Friend Request", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }

    fun readDetailNotice(noticeId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val response = SecretDiaryObject.getRetrofitSDService.readDetailNotice(noticeId)
            if(response.isSuccessful){
                response.body()?.let {
                    _detailNotice.value = it
                }
            }else {
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.d("read User Notice", "Failed: StatusCode = $statusCode, Error = $errorBody")
            }
        }
    }

}