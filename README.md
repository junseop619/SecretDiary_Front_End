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

    &emsp;2-5-2. Friend First Tab(내 친구)

    &emsp;2-5-3. Friend Second Tab(친구 추천

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

    3-6. image upload &

    3-7. debounce & throttle을 이용한 검색어 자동완성 기능 구현

    3-8. 안드로이드에서 JWT를 이용한 자동 로그인 구현 


<br><br/>

---

# 1. 소개글

## 1-1. 프로젝트 소개

Secret Diary를 통해 사진과 글로 자신에 추억을 기록해요.

친구를 추가하고 친구들끼리는 서로의 추억으로 공유해봐요.

<br></br>

## 1-2. 사용 기술

![js](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![js](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white) 

Jepack Compose, Hilt,, Retrofit2(Restful API), Coroutine, Room, Debounce & Throttle

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

> ## 2-4-1. Home Screen


















