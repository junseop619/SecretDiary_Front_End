# Secret Diary (Front-end)

해당 프로젝트의 Back-end 코드에 대한 내용은 [Secret_Diary_Back_End](https://github.com/junseop619/Secret_Diary_Back_End)을 참조해주세요

<br></br>

# 목차
1. 소개글

    1-1. 프로젝트 소개

    1-2. 사용 기술

    1-3. 사용 환경

2. App UI 구조

    2-1. MainActiviy

    2-2. SDScreen

    2-3. Security

    &emsp;2-3-1. Login Screen

    &emsp;2-3-2. Join Screen

    2-4. Home

    &emsp;2-4-1. Home Screen

    &emsp;2-4-2. Add Notice Sceen

    &emsp;2-4-3. Notice Detail Screen

    2-5. Friend

    &emsp;2-5-1. Friend Screen

    &emsp;2-5-2. Friend First Tab

    &emsp;2-5-3. Friend Second Tab

    &emsp;2-5-4. User Info Screen

    &emsp;2-5-5. User Notice Detail Screen

    2-6. Setting

    &emsp;2-6-1. Setting Screen

    &emsp;2-6-2. Update User Screen

    &emsp;2-6-3. 버전 정보

    &emsp;2-6-4. 로그아웃, 회원 탈퇴


3. 기능 구현 참고 문서(본인 블로그)

    3-1. Jetpack Compose와 Hilt 이해와 사용

    3-2. Jetpack Compose에서 UI 화면 구성과 전환

    3-3. Android Room

    3-4. Kotlin Coroutine

    3-5. Retrofit2를 이용한 안드로이드와 스프링 서버 통신

    3-6. 안드로이드 emulator에서 image 불러와서 Spring mobile app server를 이용해 DB에 저장하기 (image upload & download)

    3-7. Debounce를 이용한 검색 기능 및 검색어 자동완성 기능 구현 (Debounce & Throttle)

    3-8. 안드로이드에서 JWT를 이용한 자동 로그인 및 로그아웃 구현 


<br><br/>

---

# 1. 소개글

## 1-1. 프로젝트 소개

Secret Diary를 통해 사진과 글로 자신에 추억을 기록해요.

친구를 추가하고 친구들끼리는 서로의 추억으로 공유해봐요.

<br></br>

## 1-2. 사용 기술

![js](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![js](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white) 

Jepack Compose, Hilt,, Retrofit2(Restful API), Coroutine, Room, Glide, Debounce & Throttle

Back-End -> &emsp; ![js](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) &emsp;[Secret_Diary_Back_End](https://github.com/junseop619/Secret_Diary_Back_End)

Database -> &emsp; ![js](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)

<br></br>

## 1-3. 사용 환경

![js](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white) ![js](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)  

DBeaver

<br></br>

# 2. App UI 구조

## 2-1. MainActivity

Secret Diary 실행 시 MainActiviy가 처음으로 실행되며 SDScreen UI를 랜더링하게 됩니다.

<br></br>

## 2-2. SDScreen

SDScreen은 NavManagement.kt에 정의한 RootNavHost에 정의되어 있는 securityNav라는 navigation이 기본적으로 설정되어 있습니다.

허나, SDScreen이 최초로 실행되면 Security ViewModel에 정의한 autoLogin 함수가 실행되며 token validation이 성공하면 NavManagement.kt의 RootNavHost에 정의한 myNav로 navigation을 전환되며 HomeScreen화면이 랜더링됩니다.

하지만, autoLogin에 실패할 경우 기본적으로 정의된 securityNav를 사용하게 되며, securityNav의 route인 LoginScreen으로 이동하게 됩니다.

SDScreen.kt 파일에는 SDScreen composable과 MyBottomNavigation이 정의되어 있습니다. BottomNavigation에는 Component.kt에 정의한 BottomNavItem을 기반으로 Home, Friend, Setting으로 3개의 하단 탭바가 존재합니다.

정리해서 말씀드리자면

SDScreen의 navigation -> (securityNav, myNav)  *기본적으로는 securityNav 장착

securityNav -> {LoginScreen, JoinScreen} -> 기본 화면 = Login화면

myNav -> {securityNav에 정의된 route외의 모든 route} -> 기본 화면 = Home 화면,  

component.kt에 정의한 MainTopAppBar의 경우 route값에 대한 조건을 걸어 security관련 화면에서는 나오지 않게 설계하였고

SDScreen.kt에 정의한 MyBottomNavigation의 경우 역시, route값에 대한 조건들 걸어 특정 화면에서만 하단 탭바가 나오게 설계하였습니다.

이러한 topBar와 bottomBar를 장착한 composable은 SDScreen으로 위에서 언급한 조건들은 SDScreen composable function에 정의되어 있습니다.

*자동 로그인에 대한 내용은 항목 3-8을 참조 부탁드립니다.

<br></br>

## 2-3. Security

> ## 2-3-1. Login Screen

Login Screen은 SecurityScreen.kt의 LoginScreen composable입니다.

security system의 main이 되는 화면입니다.

JWT 인증이 성공적으로 마춰진다면 해당 화면은 로그아웃, 회원탈퇴의 경우를 제외하고는 자동로그인에 의해 생략됩니다.

JWT를 이용한 자동로그인 기능에 대해서는 항목 3-8을 참조해주시길 바랍니다.

![Login](https://github.com/user-attachments/assets/27601f39-3f88-4031-8d00-4423d9eaf7f5)

<br></br>

> ## 2-3-2. Join Screen

Join Screen은 SecurityScreen.kt의 JoinScreen composable입니다.

LoginScreen에서 회원가입 버튼을 누르면 회원가입 역할을 하는 JoinScreen으로 UI가 전환됩니다.

![Join](https://github.com/user-attachments/assets/6213cbe6-8071-4691-a25b-1cc8c96d1113)

<br></br>

## 2-4. Home

<img width="342" alt="스크린샷 2024-10-06 오후 11 30 53" src="https://github.com/user-attachments/assets/7de28fe4-b025-45ac-b779-572d3e244317">

HomeScreen composable에는 상단바에 + 모양의 Icon을 눌러 AddNoticeScreen composable로 이동하여 게시물을 작성할 수 있습니다.

상단바 아래에는 Search Notice로 게시물 검색 기능을 제공합니다.

<br></br>

> ## 2-4-1. Home Screen

HomeScreen composable에는 상단바에 + 모양의 Icon을 눌러 AddNoticeScreen composable로 이동하여 게시물을 작성할 수 있습니다.

상단바 아래에는 Search Notice로 게시물 검색 기능을 제공합니다.

<img width="348" alt="스크린샷 2024-10-06 오후 11 35 23" src="https://github.com/user-attachments/assets/fc404fd8-90b2-4b7a-b8d9-97d1b63609ca">

위 사진과 같이 해당 TextField에 text를 입력하면 해당 text에 대한 결과가 LazyColumn으로 나오게 됩니다.

해당 검색기능에 대해서는 검색어 자동완성 기능을 제공하며 아래와 같이 

<img width="363" alt="스크린샷 2024-10-06 오후 11 37 21" src="https://github.com/user-attachments/assets/c66cffeb-ba9e-4f4e-addc-34dbb9e9165b">

해당 keyword와 유사한 notice를 LazyColumn에 결과로 제공합니다.

* retrofit2를 이용한 게시물 읽어오기 기능에 관해서는 항목 --의 --을 참조 부탁드립니다.

* 검색기능 및 검색어 자동완성에 대해서는 항목 --의 --을 참조 부탁드립니다.

게시물 검색기능 아래로는 LazyColumn을 통해 게시물을 RecyclerView형식으로 자신이 작성한 게시물을 표시하게 됩니다.

검색 결과로 나온 LazyColmn이나 Home 자체의 LazyColmn에 나온 notice에 대하여, 해당 notice를 터치하면 NoticeDetailScreen composable로 이동하게 됩니다.

<br></br>

> ## 2-4-2. Add Notice Screen

Add Notice Screen의 경우 AddNoticeScreen composable로 구현하였습니다.

위에서 언급했던바와 같이 HomeScreen composable의 상단 바 + 버튼을 통해 접근이 가능합니다.

<img width="351" alt="스크린샷 2024-10-06 오후 11 43 03" src="https://github.com/user-attachments/assets/a39434b0-3d92-434a-b778-1f7a51481a96">

해당 composable에서는 제목과 내용, 사진을 첨부할 수 있습니다. 사진을 첨부하게 되면 제한조건 text 하단의 +모양이 있는 이미지가 선택한 이미지로 바뀌게 됩니다.

이후, 작성하기를 누르게 되면 retrofit2를 이용해 게시물이 Create되며 Spring을 이용하여 해당 게시물이 작성 시간과 함께 DB에 저장됩니다.

* retrofit2를 이용한 Spring과 통신하여 CRUD를 구현하는 방법에 대해서는 ~ 를 참고 부탁드립니다.

* android emulator에서 Image를 가져와 Spring을 이용해 DB에 저장하는 방법에 대해서는 ~ 를 참고 부탁드립니다.

<br></br>

> ## 2-4-3. Notice Detail Screen

Notice Detail Screen의 경우 NoticeDetailScreen composable로 구현하였습니다.

위에서 언급했던바와 같이 HomeScreen에 있는 LazyColumn으로 나오는 Notice들에 대하여 터치하면 접근하게 됩니다.

<img width="340" alt="스크린샷 2024-10-06 오후 11 51 26" src="https://github.com/user-attachments/assets/531ddee8-edc6-4620-9bdc-97212c8fec8d">

해당 화면에서는 등록할 때 저장했던 제목, 내용, 작성시간, 이미지가 나옵니다.

AddNoticeScreen에서와 마찬가지로 retrofit2를 이용한 Read의 구현입니다.

<br></br>

## 2-5. Friend

(Friend Screen img)

Friend Screen의 경우 FriendScreen coomposable입니다.

위 사진과 같이 FriendScreen에는 TabRow를 이용하여 2개의 내 친구(FriendFirstTab), 친구 추천(FriendSecondTab)으로 구성되어 있습니다.

<br></br>

> ## 2-5-1. Friend First Tab

(친구 2명정도 있고, 특정 친구 검색 결과로 나오는 모습 image)

FriendFirstTab composable로 구성한 해당 tab의 경우 위 사진과 동일한 화면으로 bottomNavigation에서 Friend tab일 때의 기본 UI입니다.

해당 tab에서의 검색 기능의 경우 현재 친구목록에서 이메일을 입력하면 해당 이메일에 해당하는 친구 정보를 표시해줍니다. 

이 검색 기능 역시 검색어 Debounce와 Throttle을 이용한 자동완성 기능을 제공하고 있습니다.

검색 영역 아래에는 내 친구 목록이라는 text가 있는데 해당 text 아래에는 LazyColumn을 이용하여 현재 친구 목록을 recyclerView 형식으로 보여줍니다. 

검색결과로 나오거나 내 친구 목록으로 나온 ListItem을 클릭하게 되면 UserInfoScreen composable로 화면이 전환되며 해당 친구의 프로필 및 작성 게시물들을 확인 할 수 있습니다.

UserInfoSceen의 경우 항목 2-5-3. User Info Screen에서 더욱 자세하게 다룰 예정입니다. 

<br></br>

> ## 2-5-2. Friend Second Tab

(친구 요청 목록 1명 정도 있는 모습 image)

FriendSecondTab composable로 구성된 tab입니다.

해당 tab에서의 검색 기능의 경우 이메일을 입력하면 해당 이메일에 해당하는 모든 유저를 표시해줍니다.

이 검색 기능 역시 검색어 Debounce와 Throttle을 이용한 자동완성 기능을 제공하고 있습니다.

친구 요청 목록의 경우 역시 LazyColumn으로 설계되었으며 현재 본인에게 친구 요청한 유저를 보여주며 수락 버튼을 누르게 되면 해당 유저와 친구가 되고 요청목록에는 해당 요청이 삭제되고, tab1의 친구 목록에는 추가가 되는 시스템입니다.

검색 결과로 나오거나 요청 목록으로 나온 ListItem을 클릭하게 되면 tab1과 같이  UserInfoScreen composable로 화면이 전환됩니다.

<br></br>

> ## 2-5-3. User Info Screen

![준섭2](https://github.com/user-attachments/assets/21cb5175-7a80-41da-b78e-42832476dfc3)

UserInfoScreen composable로 구성되어 있는 User Info Screen의 경우 위 friend의 각 tab에서 LazyColumn안에 있는 ListItem을 터치하면 나오는 UI입니다.

해당 composable에서는 LaunchedEffect에 의해 해당 Composable이 구성되자 마자, Room을 이용해 현재 로그인한 유저의 email을 갖고 온 후 ListItem에서 선택한 유저의 email을 함께 parameter로 하여 1차적으로 친구 관계 여부를 파악하게 됩니다.

친구 관계 여부는 friendViewModel에 정의한 checkMyFriend method를 이용해 수행하게 됩니다.

친구관계가 아닐 경우 해당 유저의 위 왼쪽 사진과 같이 해당 유저의 프로필과 친구 요청 버튼이 활성화되어있습니다. 해당 버튼을 누르게 되면, 해당 버튼이 비활성화 되며 상대방의 Friend Second tab의 친구 요청 목록에 해당 요청이 추가됩니다.

(버튼 비활성화 되어있는 사진)

만약 친구 관계일 경우 오른 위 오른쪽 사진과 같이 해당 유저의 프로필과 작성한 게시물이 LazyColumn형식으로 나오게 됩니다.

해당 게시물을 터치하게 되면 다음 항목에서 설명할 User Notice Detail Screen으로 전환되게 됩니다. 

<br></br>

> ## 2-5-4. User Notice Detail Screen

User Notice Detail Screen의 경우 FriendScreen.kt에 정의한 UserNoticeDetailScreen composable에 의해 구성됩니다.

해당 composable의 경우 항목 2-4-3. Notice Detail Screen과 별다른 차이점은 없으므로 추가적인 설명은 생략하겠습니다.

<br></br>

## 2-6. Setting

> ## 2-6-1. Settomg Screem

(setting Screen img)

Setting Sreen의 경우 SettingScreen.kt에 정의한 SettingScreen composable에 의해 구성됩니다.

Setting Screen의 상단에는 사용자의 프로필이 나오게 되고, 내 정보 수정하기 버튼을 눌러 다음 항목에서 설명 할 Update User Screen에 접근하여 내 정보를 수정 할 수 있습니다.

Setting Screen에서 유저 정보가 나오는 원리는 Launched Effect를 이용하여 해당 composable이 구성되자마자 Launched Effect안에 감싸진 내용이 작동되게 하며,

감싸져있는 Room method를 이용해 현재 로그인 한 유저의 정보를 받아와 settingViewModel에 정의한 loadUserInfo method를 이용해 해당 정보를 기반으로 Spring을 이용하여 유저 정보를 받아오게 됩니다.

내 프로필 아래로는 LazyColumn을 이용하여 Secret Diary의 다양한 Service를 제옹하게 됩니다.

버전 정보, 로그아웃, 회원 탈퇴기능을 제공하는데 해당 기능들 모두 Dialog를 통해 service를 제공하고 있습니다.

<br></br>

> ## 2-6-2. Update User Screen

(update User screen img)

Update User Screen의 경우 SettingScreen.kt에 정의한 UpdateUserScreen composable에 의해 구성됩니다.

해당 composable에서는 해당 유저의 이름과 소개말, 프로필 사진을 변경할 수 있는 기능을 제공합니다.

<br></br>

> ## 2-6-3. 버전 정보

(dialog img)

버전 정보의 경우 SettingScreen에서 remember, mutableStateOf를 이용해 UI상태를 관리하며 VersionInfoDialog composable을 이용해 Dialog창으로 UI를 구현하고 있습니다.

해당 UI에서는 BuildConfig에서 VERSION_NAME을 이용해 버전 정보를 불러와 표시합니다.

<br></br>

> ## 2-6-4. 로그아웃, 회원 탈퇴

![준섭1](https://github.com/user-attachments/assets/2de1db24-4614-47ce-bb0e-de4bfbea2071)

로그아웃과 회원탈퇴 역시 remember, mutableStateOf를 이용해 UI상태를 관리하며 LogOutDialog, DeleteUserDialog를 이용해 Dialog창으로 UI를 구현하고 있습니다.

위 사진에서 모두 '예'를 누르면 각 로그아웃과 회원탈퇴 settingViewModel에 정의된 logout, deleteUser method가 실현되게 됩니다.

logout method의 경우 sharedPreferences에 의해 저장된 autoLogin을 위한 token이 삭제되며, spring과 연계하여 로그아웃 기능이 작동하게 됩니다.

이에 대한 기술적 구현에 대한 내용은 항목 3-8. 안드로이드에서 JWT를 이용한 자동 로그인 및 로그아웃 구현을 참고 부탁드립니다.

delete method의 경우 deleteUser method를 호출하여 retrofit2의 DELETE method를 이용해 spring과 연계하여 DB에 존재하는 회원 정보를 삭제하여 회원탈퇴 기능을 구현합니다. 


<br></br>

# 3. 기능 구현 참고 문서 (본인 블로그)

> ## 3-1. Jetpack Compose와 Hilt 이해와 사용

Secret Diary Project를 진행하며 사용한 Jetpack Compose와 Hilt에 대하여는 

제가 블로그에 직접 작성했던 [Jetpack Compose와 Hilt 이해와 사용](https://pinlib.tistory.com/entry/jetpackcomposeandjhilt)를 참고 부탁드립니다. 

<br></br>

> ## 3-2. Jetpack Compose에서 UI 화면 구성과 전환

Jetpack Compose를 이용하여 제작한 Secret Diary에서 UI 화면을 구성한 방법과 화면 전환에 대한 내용에 대해서는

제가 블로그에 직접 작성했던 [Jetpack Compose에서 UI 화면 구성과 전환(scaffold, box, navHost)](https://pinlib.tistory.com/entry/Jetpack-Compose2)를 참고 부탁드립니다.  

<br></br>

> ## 3-3. Android Room

Secret Diary project를 진행하며 Room을 사용한 경우는 로그인한 유저가 본인이 작업한 내용(작성한 게시물, 본인 프로필, 내 친구 및 친구 요청관리) 등에서 본인 정보를 가져오는 경우였습니다.

해당 방법에서 대부분 가장 최근 로그인 한 유저의 email값을 Room에 저장하고 불러와서 해당 값을 parameter로 본인이 작성한 게시물 등을 불러오는 network 작업을 수행하였습니다.

Room에 대한 개념이나 사용 방법, 사용 예시에 관해서는 제가 블로그에 직접 작성했던 [Android Room 사용하기
](https://pinlib.tistory.com/entry/android-room)를 참고 부탁드립니다.

<br></br>

> ## 3-4. Kotlin Coroutine

Room method를 호출 할 때나, Retrofit2를 이용한 network 통신 등을 이용 할 때는 Kotlin Coroutine을 이용한 비동기 처리를 사용해야 합니다.

저 역시도, 해당 프로젝트의 많은 상황에서 Couroutine을 사용하였습니다.

Kotlin Couroutine에 대한 개념이나 사용 방법, 사용 예시에 관해서는 제가 블로그에 직접 작성했던 [Kotlin Coruoutins과 비동기에 관하여](https://pinlib.tistory.com/entry/Kotlin-coroutine)를 참고 부탁드립니다.

<br></br>

> ## 3-5. Retrofit2를 이용한 안드로이드와 스프링 서버통신

해당 프로젝트인 Secret Diary의 경우 Android 기반 Front-end와 Spring 기반 Back-end로 나뉘어져 있습니다.

이 두 프로그램은 retrofit2 API를 통해 서로 통신하고 있습니다.

해당 과정에 대한 개념이나 예시들에 대해서는 아래의 링크를 참고 부탁 드립니다.

* [Retrofit2를 이용한 안드로이드와 스프링 서버 통신(안드로이드편)(안드로이드 서버통신)](https://pinlib.tistory.com/entry/retrofit2-1) (블로그)

* [Retrofit2를 이용한 안드로이드와 스프링 서버 통신(스프링편)(안드로이드 서버통신)](https://pinlib.tistory.com/entry/retrofit2-2) (블로그)

* [Secret_Diary_Back_End](https://github.com/junseop619/Secret_Diary_Back_End) (깃허브)


<br></br>

> ## 3-6. 안드로이드 emulator에서 image 불러와서 Spring mobile app server를 이용해 DB에 저장하기 (image upload & download)

Secret Diary는 각 유저들이 자신의 일기를 작성할 수 있는 service를 제공하고 있습니다.

이러한 service는 각 유저들의 프로필 이미지나 일기장 속 넣고 싶은 이미지 등을 upload 하거나 download하는 기능을 필요로 합니다.

image를 upload하거나 download하는 기능에 대한 기념이나 예시들에 대해서는 제가 블로그에 직접 작성했던 [안드로이드 에뮬레이터에서 이미지 불러와서 Spring 모바일 앱 서버를 이용해 DB에 저장하기](https://pinlib.tistory.com/entry/image-upload-download)를 참고 부탁드립니다.

<br></br>

> ## 3-7. Debounce를 이용한 검색 기능 및 검색어 자동완성 기능 구현 (Debounce & Throttle)

Secret Diary는 HomeScreen에서는 게시물 제목을 keyword로 자신이 작성한 게시물을 검색할 수 있는 기능을 제공하고 있고,

FriendScreen에서의 Tab1, Tab2 역시 각 친구 검색과 유저 검색 기능을 제공하고 있습니다.

이러한 기능에 대하여 Debounce를 이용하여 구현을 하였습니다.

Debounce를 이용한 검색 기능 및 검색어 자동완성 기능 구현과 Throttle의 개념에 대해서는 제가 블로그에 직접 작성했던 [안드로이드에서 검색기능 및 검색어 자동완성 기능 구현하기 (Debounce & Throttle](https://pinlib.tistory.com/entry/debounceandthrottle)를 참고 부탁드립니다.

<br></br>

> ## 3-8. 안드로이드에서 JWT를 이용한 자동 로그인 및 로그아웃 구현

ㅁㅁㅁㅁㅁㅁㅁ

<br></br>
 






















